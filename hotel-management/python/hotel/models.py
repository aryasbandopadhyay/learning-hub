"""Domain models: Hotel, Room, Reservation, room types, and reservation states."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import date
from enum import Enum
from typing import Any

from .exceptions import InvalidReservationStateError, RoomUnavailableError


class RoomType(Enum):
    """Room categories with base nightly rates kept explicit for the MVP."""

    STANDARD = 100
    DELUXE = 180
    SUITE = 300

    @property
    def nightly_rate(self) -> int:
        return self.value


class ReservationStatus(Enum):
    CONFIRMED = "CONFIRMED"
    CHECKED_IN = "CHECKED_IN"
    CHECKED_OUT = "CHECKED_OUT"
    CANCELLED = "CANCELLED"


@dataclass
class Reservation:
    """A booking for one room and a half-open range [check_in, check_out)."""

    room: "Room"
    check_in: date
    check_out: date
    total_price: int
    id: str = field(default_factory=lambda: str(uuid.uuid4()))
    status: ReservationStatus = ReservationStatus.CONFIRMED

    def overlaps(self, other_check_in: date, other_check_out: date) -> bool:
        # Canonical rule: overlap iff start1 < end2 AND start2 < end1. Checkout day is free.
        return self.check_in < other_check_out and other_check_in < self.check_out

    def blocks_availability(self) -> bool:
        return self.status in {ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN}

    def check_in_reservation(self) -> None:
        if self.status is not ReservationStatus.CONFIRMED:
            raise InvalidReservationStateError("Only CONFIRMED reservations can check in")
        self.status = ReservationStatus.CHECKED_IN

    def check_out_reservation(self) -> None:
        if self.status is not ReservationStatus.CHECKED_IN:
            raise InvalidReservationStateError("Only CHECKED_IN reservations can check out")
        self.status = ReservationStatus.CHECKED_OUT

    def cancel(self) -> None:
        if self.status is ReservationStatus.CHECKED_OUT:
            raise InvalidReservationStateError("CHECKED_OUT reservations cannot be cancelled")
        self.status = ReservationStatus.CANCELLED


class Room:
    """A single room. THIS CLASS IS THE CONCURRENCY BOUNDARY.

    The reservation list is protected by a per-room Lock. Booking holds that lock while checking for
    an overlapping live reservation and appending the new reservation, so two threads cannot both
    observe the room as free for the same date range.
    """

    def __init__(self, room_id: str, room_type: RoomType) -> None:
        self.id = room_id
        self.room_type = room_type
        self._reservations: list[Reservation] = []
        self._lock = threading.Lock()

    def is_available(self, check_in: date, check_out: date) -> bool:
        _validate_range(check_in, check_out)
        with self._lock:
            return not any(r.blocks_availability() and r.overlaps(check_in, check_out) for r in self._reservations)

    def book(self, check_in: date, check_out: date, pricing_strategy: Any) -> Reservation:
        """Atomically reserve [check_in, check_out), or raise if a live reservation overlaps."""
        _validate_range(check_in, check_out)
        with self._lock:
            if any(r.blocks_availability() and r.overlaps(check_in, check_out) for r in self._reservations):
                raise RoomUnavailableError(f"Room {self.id} is unavailable for the requested dates")
            price = pricing_strategy.calculate_price(self, check_in, check_out)
            reservation = Reservation(self, check_in, check_out, price)
            self._reservations.append(reservation)
            return reservation

    @property
    def reservations(self) -> tuple[Reservation, ...]:
        with self._lock:
            return tuple(self._reservations)


@dataclass(frozen=True)
class Hotel:
    name: str
    rooms: tuple[Room, ...]

    def __init__(self, name: str, rooms) -> None:
        object.__setattr__(self, "name", name)
        object.__setattr__(self, "rooms", tuple(rooms))


def _validate_range(check_in: date, check_out: date) -> None:
    if check_in is None or check_out is None or check_in >= check_out:
        raise ValueError("check_in must be before check_out")
