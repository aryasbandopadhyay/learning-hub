"""Domain models: cabins, seats, flight inventory, flights, passengers, and bookings."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from typing import Optional


class Cabin(Enum):
    ECONOMY = "ECONOMY"
    BUSINESS = "BUSINESS"


class SeatStatus(Enum):
    AVAILABLE = "AVAILABLE"
    BOOKED = "BOOKED"


@dataclass(frozen=True)
class Passenger:
    """Passenger details kept intentionally minimal for machine-coding focus."""

    name: str
    email: str


class Seat:
    """A single aircraft seat. THIS CLASS IS THE CONCURRENCY BOUNDARY.

    ``try_book`` holds ``self._lock`` while checking AVAILABLE and flipping the state to BOOKED.
    Therefore 50 threads racing for the same seat cannot double-book it: one lock holder wins, the
    rest observe BOOKED and fail.
    """

    def __init__(self, seat_no: str, cabin: Cabin) -> None:
        self.seat_no = seat_no
        self.cabin = cabin
        self._status = SeatStatus.AVAILABLE
        self._passenger: Optional[Passenger] = None
        self._lock = threading.Lock()

    def try_book(self, passenger: Passenger) -> bool:
        """Atomically claim this seat; return True only for the winning thread."""
        with self._lock:
            if self._status is SeatStatus.BOOKED:
                return False
            self._status = SeatStatus.BOOKED
            self._passenger = passenger
            return True

    def free(self) -> None:
        """Atomically release the seat during cancellation."""
        with self._lock:
            self._status = SeatStatus.AVAILABLE
            self._passenger = None

    @property
    def is_available(self) -> bool:
        with self._lock:
            return self._status is SeatStatus.AVAILABLE

    @property
    def status(self) -> SeatStatus:
        with self._lock:
            return self._status

    @property
    def passenger(self) -> Optional[Passenger]:
        with self._lock:
            return self._passenger


class FlightInventory:
    """Fixed seat map for one flight.

    No coarse inventory lock is required for booking because every Seat owns its atomic state
    transition. Scans are snapshots, so every successful path still calls ``try_book``.
    """

    def __init__(self, seats: list[Seat]) -> None:
        self._seats_by_no = {seat.seat_no: seat for seat in seats}

    def find_seat(self, seat_no: str) -> Optional[Seat]:
        return self._seats_by_no.get(seat_no)

    def has_available_seat(self) -> bool:
        return any(seat.is_available for seat in self._seats_by_no.values())

    def available_seats(self, cabin: Cabin) -> list[Seat]:
        return sorted(
            [seat for seat in self._seats_by_no.values() if seat.cabin is cabin and seat.is_available],
            key=lambda seat: seat.seat_no,
        )

    def try_book_seat(self, seat_no: str, passenger: Passenger) -> Optional[Seat]:
        seat = self._seats_by_no.get(seat_no)
        if seat is None or not seat.try_book(passenger):
            return None
        return seat

    def try_book_any(self, cabin: Cabin, passenger: Passenger) -> Optional[Seat]:
        for seat in sorted(self._seats_by_no.values(), key=lambda s: s.seat_no):
            if seat.cabin is cabin and seat.try_book(passenger):
                return seat
        return None

    def available_count(self) -> int:
        return sum(1 for seat in self._seats_by_no.values() if seat.is_available)

    @property
    def seats(self) -> tuple[Seat, ...]:
        return tuple(self._seats_by_no.values())


@dataclass(frozen=True)
class Flight:
    flight_number: str
    origin: str
    destination: str
    departure_time: datetime
    inventory: FlightInventory

    def matches(self, origin: str, destination: str, date=None) -> bool:
        route_matches = self.origin.lower() == origin.lower() and self.destination.lower() == destination.lower()
        date_matches = date is None or self.departure_time.date() == date
        return route_matches and date_matches


@dataclass(frozen=True)
class Booking:
    """Immutable PNR record created after the seat is atomically marked BOOKED."""

    flight_number: str
    seat_no: str
    passenger: Passenger
    cabin: Cabin
    price: int
    booked_at: datetime
    pnr: str = field(default_factory=lambda: "PNR-" + uuid.uuid4().hex[:8].upper())
