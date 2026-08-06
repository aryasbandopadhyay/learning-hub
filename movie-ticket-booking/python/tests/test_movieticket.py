"""End-to-end tests for the Movie Ticket Booking MVP, including expiry and concurrency."""

from __future__ import annotations

import threading
from datetime import datetime, timedelta, timezone

import pytest

from movieticket.exceptions import InvalidBookingStateError, PaymentRejectedError, SeatUnavailableError
from movieticket.models import SeatStatus, Show
from movieticket.payment import AlwaysSuccessPaymentProcessor, FailingPaymentProcessor
from movieticket.service import BookingService
from movieticket.states import BookingState


class MutableClock:
    """Hand-advanced clock so hold-expiry tests are deterministic (no sleeps)."""

    def __init__(self, start: datetime) -> None:
        self._now = start

    def __call__(self) -> datetime:
        return self._now

    def advance(self, delta: timedelta) -> None:
        self._now += delta


def make_service(show: Show, clock=None) -> BookingService:
    return BookingService(
        [show],
        AlwaysSuccessPaymentProcessor(),
        clock or (lambda: datetime.now(timezone.utc)),
        timedelta(minutes=5),
    )


def test_happy_path_holds_then_pays_and_books_seats():
    show = Show("S1", 2, 2, 200)
    service = make_service(show)

    booking = service.create_booking("S1", ["R1C1", "R1C2"], "U1")
    assert booking.state is BookingState.SEATS_HELD
    assert booking.total_price == 400
    assert show.seat("R1C1").status is SeatStatus.HELD

    service.pay(booking.id, "PAY-1")
    assert booking.state is BookingState.CONFIRMED
    assert show.seat("R1C1").status is SeatStatus.BOOKED
    assert show.seat("R1C2").status is SeatStatus.BOOKED


def test_payment_failure_marks_failed_and_releases_seats():
    show = Show("S1", 1, 2, 200)
    service = BookingService([show], FailingPaymentProcessor(), hold_window=timedelta(minutes=5))
    booking = service.create_booking("S1", ["R1C1"], "U1")

    with pytest.raises(PaymentRejectedError):
        service.pay(booking.id, "PAY-BAD")

    assert booking.state is BookingState.FAILED
    assert show.seat("R1C1").status is SeatStatus.AVAILABLE


def test_expired_hold_rejects_payment_and_releases_seats():
    clock = MutableClock(datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc))
    show = Show("S1", 1, 2, 200)
    service = make_service(show, clock)
    booking = service.create_booking("S1", ["R1C1"], "U1")
    clock.advance(timedelta(minutes=6))

    with pytest.raises(InvalidBookingStateError):
        service.pay(booking.id, "PAY-LATE")

    assert booking.state is BookingState.EXPIRED
    assert show.seat("R1C1").status is SeatStatus.AVAILABLE


def test_illegal_transition_paying_confirmed_booking_is_rejected():
    show = Show("S1", 1, 2, 200)
    service = make_service(show)
    booking = service.create_booking("S1", ["R1C1"], "U1")
    service.pay(booking.id, "PAY-1")

    with pytest.raises(InvalidBookingStateError):
        service.pay(booking.id, "PAY-AGAIN")
    assert booking.state is BookingState.CONFIRMED


def test_all_or_nothing_hold_leaves_other_requested_seats_available():
    show = Show("S1", 1, 3, 200)
    service = make_service(show)
    service.create_booking("S1", ["R1C1"], "U1")

    with pytest.raises(SeatUnavailableError):
        service.create_booking("S1", ["R1C1", "R1C2"], "U2")

    assert show.seat("R1C1").status is SeatStatus.HELD
    assert show.seat("R1C2").status is SeatStatus.AVAILABLE


def test_scheduler_expires_stale_bookings():
    clock = MutableClock(datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc))
    show = Show("S1", 1, 2, 200)
    service = make_service(show, clock)
    booking = service.create_booking("S1", ["R1C1"], "U1")
    clock.advance(timedelta(minutes=6))

    assert service.expire_stale_bookings(clock()) == 1
    assert booking.state is BookingState.EXPIRED
    assert show.seat("R1C1").status is SeatStatus.AVAILABLE


def test_concurrent_booking_never_double_holds_or_books_same_seat():
    threads = 50
    show = Show("S1", 1, 1, 200)
    service = make_service(show)
    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()
        try:
            booking = service.create_booking("S1", ["R1C1"], f"U{i}")
            service.pay(booking.id, f"PAY-{i}")
            with successes_lock:
                successes.append(booking.id)
        except SeatUnavailableError:
            pass

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert len(successes) == 1, "exactly one booking should win the single seat"
    assert len(set(successes)) == 1, "winner booking id is unique"
    assert show.seat("R1C1").status is SeatStatus.BOOKED
