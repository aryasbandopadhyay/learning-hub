"""Domain models: Show, Seat, and Booking."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from typing import Iterable

from .states import BookingState, ensure_can_move


class SeatStatus(Enum):
    AVAILABLE = "AVAILABLE"
    HELD = "HELD"
    BOOKED = "BOOKED"


@dataclass
class Seat:
    """One physical seat. Service methods mutate it only while holding the parent Show lock."""

    id: str
    status: SeatStatus = SeatStatus.AVAILABLE
    booking_id: str | None = None

    def hold_for(self, booking_id: str) -> None:
        self.status = SeatStatus.HELD
        self.booking_id = booking_id

    def book_for(self, booking_id: str) -> None:
        self.status = SeatStatus.BOOKED
        self.booking_id = booking_id

    def release(self) -> None:
        self.status = SeatStatus.AVAILABLE
        self.booking_id = None


class Show:
    """A fixed grid of seats for one show.

    The RLock is the inventory boundary: hold/pay/expire check and mutate seats while holding it,
    so a multi-seat hold is all-or-nothing even under 50 racing threads.
    """

    def __init__(self, show_id: str, rows: int, cols: int, price_per_seat: int) -> None:
        self.id = show_id
        self.rows = rows
        self.cols = cols
        self.price_per_seat = price_per_seat
        self._lock = threading.RLock()
        self._seats: dict[str, Seat] = {
            f"R{r}C{c}": Seat(f"R{r}C{c}")
            for r in range(1, rows + 1)
            for c in range(1, cols + 1)
        }

    @property
    def lock(self) -> threading.RLock:
        return self._lock

    def seat(self, seat_id: str) -> Seat | None:
        return self._seats.get(seat_id)

    @property
    def seats(self) -> tuple[Seat, ...]:
        return tuple(self._seats.values())


@dataclass
class Booking:
    """Booking aggregate containing user intent, held seats, total, expiry, and current state."""

    show_id: str
    seat_ids: tuple[str, ...]
    user_id: str
    total_price: int
    hold_expires_at: datetime
    id: str = field(default_factory=lambda: str(uuid.uuid4()))
    state: BookingState = BookingState.CREATED
    payment_ref: str | None = None
    _lock: threading.RLock = field(default_factory=threading.RLock, repr=False)

    @classmethod
    def create(
        cls, show_id: str, seat_ids: Iterable[str], user_id: str, total_price: int, hold_expires_at: datetime
    ) -> "Booking":
        return cls(show_id, tuple(seat_ids), user_id, total_price, hold_expires_at)

    def transition_to(self, target: BookingState) -> None:
        with self._lock:
            ensure_can_move(self.state, target)
            self.state = target
