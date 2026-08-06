"""Application service for discovery, seat holds, expiry, and booking confirmation."""

from __future__ import annotations

import threading
import uuid
from datetime import datetime, timedelta, timezone
from typing import Callable

from .exceptions import HoldExpiredError, NotFoundError, SeatUnavailableError
from .models import Booking, City, Seat, SeatHold, SeatStatus, Show


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class BookMyShowService:
    """Coordinates in-memory discovery and booking.

    The centerpiece is thread safety: ``hold_seats`` and ``confirm_booking`` take the target
    ``Show.lock`` and perform expiry cleanup, validation, and mutation in that one critical section.
    That makes a multi-seat hold all-or-nothing and prevents double-booking under races.
    """

    def __init__(
        self,
        clock: Callable[[], datetime] = _utc_now,
        hold_duration: timedelta = timedelta(minutes=5),
    ) -> None:
        self._clock = clock
        self._hold_duration = hold_duration
        self._cities: dict[str, City] = {}
        self._shows: dict[str, Show] = {}
        self._holds: dict[str, SeatHold] = {}
        self._bookings: dict[str, Booking] = {}
        self._catalog_lock = threading.Lock()
        self._holds_lock = threading.Lock()
        self._bookings_lock = threading.Lock()

    def add_city(self, city: City) -> None:
        with self._catalog_lock:
            self._cities[city.id] = city
            for theater in city.theaters:
                for screen in theater.screens:
                    for show in screen.shows:
                        self._shows[show.id] = show

    def search_shows(self, city_name: str, movie_title: str) -> list[Show]:
        city_key = city_name.casefold()
        movie_key = movie_title.casefold()
        result: list[Show] = []
        with self._catalog_lock:
            cities = tuple(self._cities.values())
        for city in cities:
            if city.name.casefold() != city_key:
                continue
            for theater in city.theaters:
                for screen in theater.screens:
                    for show in screen.shows:
                        if show.movie.title.casefold() == movie_key:
                            result.append(show)
        return result

    def hold_seats(self, show_id: str, seat_ids: list[str], user_id: str) -> SeatHold:
        show = self._find_show(show_id)
        with show.lock:
            now = self._clock()
            self._release_expired_for_show(show, now)
            for seat_id in seat_ids:
                seat = self._require_seat(show, seat_id)
                if not seat.is_available():
                    raise SeatUnavailableError(f"Seat not available: {seat_id}")
            hold_id = str(uuid.uuid4())
            expires_at = now + self._hold_duration
            for seat_id in seat_ids:
                show.seats[seat_id].hold(hold_id, user_id, expires_at)
            hold = SeatHold(hold_id, show_id, tuple(seat_ids), user_id, expires_at)
            with self._holds_lock:
                self._holds[hold_id] = hold
            return hold

    def confirm_booking(self, hold_id: str, payment_ref: str) -> Booking:
        with self._holds_lock:
            hold = self._holds.get(hold_id)
        if hold is None:
            raise NotFoundError(f"Unknown hold: {hold_id}")
        show = self._find_show(hold.show_id)
        with show.lock:
            now = self._clock()
            if hold.expires_at <= now:
                self._release_hold_seats(show, hold)
                with self._holds_lock:
                    self._holds.pop(hold_id, None)
                raise HoldExpiredError(f"Hold expired: {hold_id}")
            for seat_id in hold.seat_ids:
                seat = self._require_seat(show, seat_id)
                if not seat.is_held_by(hold_id):
                    raise SeatUnavailableError(f"Seat is no longer held by this hold: {seat_id}")
            for seat_id in hold.seat_ids:
                show.seats[seat_id].book()
            booking = Booking(
                hold_id=hold.id,
                show_id=hold.show_id,
                seat_ids=hold.seat_ids,
                user_id=hold.user_id,
                payment_ref=payment_ref,
                booked_at=now,
            )
            with self._bookings_lock:
                self._bookings[booking.id] = booking
            with self._holds_lock:
                self._holds.pop(hold_id, None)
            return booking

    def release_expired_holds(self, now: datetime) -> None:
        for show in tuple(self._shows.values()):
            with show.lock:
                self._release_expired_for_show(show, now)

    def seat_status(self, show_id: str, seat_id: str) -> SeatStatus:
        show = self._find_show(show_id)
        with show.lock:
            self._release_expired_for_show(show, self._clock())
            return self._require_seat(show, seat_id).status

    @property
    def bookings(self) -> dict[str, Booking]:
        with self._bookings_lock:
            return dict(self._bookings)

    def _release_expired_for_show(self, show: Show, now: datetime) -> None:
        with self._holds_lock:
            holds_snapshot = tuple(self._holds.values())
        for hold in holds_snapshot:
            if hold.show_id == show.id and hold.expires_at <= now:
                self._release_hold_seats(show, hold)
                with self._holds_lock:
                    self._holds.pop(hold.id, None)

    def _release_hold_seats(self, show: Show, hold: SeatHold) -> None:
        for seat_id in hold.seat_ids:
            seat = show.seats.get(seat_id)
            if seat is not None and seat.is_held_by(hold.id):
                seat.release_hold()

    def _find_show(self, show_id: str) -> Show:
        show = self._shows.get(show_id)
        if show is None:
            raise NotFoundError(f"Unknown show: {show_id}")
        return show

    @staticmethod
    def _require_seat(show: Show, seat_id: str) -> Seat:
        seat = show.seats.get(seat_id)
        if seat is None:
            raise NotFoundError(f"Unknown seat: {seat_id}")
        return seat
