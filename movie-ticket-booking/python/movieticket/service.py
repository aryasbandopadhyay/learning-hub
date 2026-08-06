"""BookingService orchestrates hold -> pay -> confirm/fail/expire."""

from __future__ import annotations

from datetime import datetime, timedelta, timezone
from typing import Callable, Iterable, Sequence

from .exceptions import BookingNotFoundError, InvalidBookingStateError, PaymentRejectedError, SeatUnavailableError
from .models import Booking, SeatStatus, Show
from .payment import PaymentProcessor
from .states import BookingState


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class BookingService:
    """Application service and concurrency coordinator.

    Locking rule: acquire Show.lock before changing seats. While under that lock, use the Booking's
    state-machine method for transitions. This mirrors the Java implementation 1:1.
    """

    def __init__(
        self,
        shows: Sequence[Show],
        payment_processor: PaymentProcessor,
        clock: Callable[[], datetime] = _utc_now,
        hold_window: timedelta = timedelta(minutes=5),
    ) -> None:
        self._shows = {show.id: show for show in shows}
        self._bookings: dict[str, Booking] = {}
        self._payment_processor = payment_processor
        self._clock = clock
        self._hold_window = hold_window

    def create_booking(self, show_id: str, seat_ids: Iterable[str], user_id: str) -> Booking:
        """Atomically hold all requested seats or none."""
        ids = tuple(seat_ids)
        show = self.get_show(show_id)
        with show.lock:
            for seat_id in ids:
                seat = show.seat(seat_id)
                if seat is None or seat.status is not SeatStatus.AVAILABLE:
                    raise SeatUnavailableError(f"Seat not available: {seat_id}")

            booking = Booking.create(
                show_id,
                ids,
                user_id,
                len(ids) * show.price_per_seat,
                self._clock() + self._hold_window,
            )
            booking.transition_to(BookingState.SEATS_HELD)
            for seat_id in ids:
                seat = show.seat(seat_id)
                assert seat is not None
                seat.hold_for(booking.id)
            self._bookings[booking.id] = booking
            return booking

    def pay(self, booking_id: str, payment_ref: str) -> Booking:
        booking = self.get_booking(booking_id)
        show = self.get_show(booking.show_id)
        with show.lock:
            self._expire_if_needed_locked(show, booking, self._clock())
            if booking.state is not BookingState.SEATS_HELD:
                raise InvalidBookingStateError(f"Cannot pay booking in state {booking.state.value}")

            booking.transition_to(BookingState.PAYMENT_PENDING)
            booking.payment_ref = payment_ref
            result = self._payment_processor.process(booking, payment_ref)
            if result.success:
                for seat_id in booking.seat_ids:
                    seat = show.seat(seat_id)
                    assert seat is not None
                    seat.book_for(booking.id)
                booking.transition_to(BookingState.CONFIRMED)
                return booking

            self._release_seats(show, booking)
            booking.transition_to(BookingState.FAILED)
            raise PaymentRejectedError(result.message)

    def expire_stale_bookings(self, now: datetime) -> int:
        expired = 0
        for booking in tuple(self._bookings.values()):
            show = self.get_show(booking.show_id)
            with show.lock:
                if self._expire_if_needed_locked(show, booking, now):
                    expired += 1
        return expired

    def _expire_if_needed_locked(self, show: Show, booking: Booking, now: datetime) -> bool:
        if booking.state in {BookingState.SEATS_HELD, BookingState.PAYMENT_PENDING} and now > booking.hold_expires_at:
            self._release_seats(show, booking)
            booking.transition_to(BookingState.EXPIRED)
            return True
        return False

    def _release_seats(self, show: Show, booking: Booking) -> None:
        for seat_id in booking.seat_ids:
            seat = show.seat(seat_id)
            if seat is not None and seat.booking_id == booking.id:
                seat.release()

    def get_booking(self, booking_id: str) -> Booking:
        try:
            return self._bookings[booking_id]
        except KeyError as exc:
            raise BookingNotFoundError(f"Unknown booking: {booking_id}") from exc

    def get_show(self, show_id: str) -> Show:
        try:
            return self._shows[show_id]
        except KeyError as exc:
            raise ValueError(f"Unknown show: {show_id}") from exc
