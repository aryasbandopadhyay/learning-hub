"""Domain models for City → Theater → Screen → Show and seat booking."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum


class SeatStatus(Enum):
    AVAILABLE = "AVAILABLE"
    HELD = "HELD"
    BOOKED = "BOOKED"


@dataclass(frozen=True)
class Movie:
    id: str
    title: str


@dataclass
class Seat:
    """A seat's fields are mutated only while the owning Show lock is held."""

    row: str
    number: int
    status: SeatStatus = SeatStatus.AVAILABLE
    hold_id: str | None = None
    held_by_user_id: str | None = None
    hold_expires_at: datetime | None = None

    @property
    def id(self) -> str:
        return f"{self.row}{self.number}"

    def is_available(self) -> bool:
        return self.status is SeatStatus.AVAILABLE

    def is_held_by(self, hold_id: str) -> bool:
        return self.status is SeatStatus.HELD and self.hold_id == hold_id

    def hold(self, hold_id: str, user_id: str, expires_at: datetime) -> None:
        self.status = SeatStatus.HELD
        self.hold_id = hold_id
        self.held_by_user_id = user_id
        self.hold_expires_at = expires_at

    def book(self) -> None:
        self.status = SeatStatus.BOOKED
        self.hold_id = None
        self.held_by_user_id = None
        self.hold_expires_at = None

    def release_hold(self) -> None:
        if self.status is SeatStatus.HELD:
            self.status = SeatStatus.AVAILABLE
            self.hold_id = None
            self.held_by_user_id = None
            self.hold_expires_at = None


class Show:
    """Seat-locking aggregate.

    One ``threading.Lock`` protects the entire seat map. It intentionally serializes operations
    within a show so multi-seat holds can validate and mutate all requested seats atomically.
    Different shows still proceed independently.
    """

    def __init__(self, id: str, movie: Movie, start_time: datetime, seats: list[Seat]) -> None:
        self.id = id
        self.movie = movie
        self.start_time = start_time
        self.seats: dict[str, Seat] = {seat.id: seat for seat in seats}
        self.lock = threading.Lock()


@dataclass
class Screen:
    id: str
    name: str
    shows: list[Show] = field(default_factory=list)

    def add_show(self, show: Show) -> None:
        self.shows.append(show)


@dataclass
class Theater:
    id: str
    name: str
    screens: list[Screen] = field(default_factory=list)

    def add_screen(self, screen: Screen) -> None:
        self.screens.append(screen)


@dataclass
class City:
    id: str
    name: str
    theaters: list[Theater] = field(default_factory=list)

    def add_theater(self, theater: Theater) -> None:
        self.theaters.append(theater)


@dataclass(frozen=True)
class SeatHold:
    id: str
    show_id: str
    seat_ids: tuple[str, ...]
    user_id: str
    expires_at: datetime


@dataclass(frozen=True)
class Booking:
    hold_id: str
    show_id: str
    seat_ids: tuple[str, ...]
    user_id: str
    payment_ref: str
    booked_at: datetime
    id: str = field(default_factory=lambda: str(uuid.uuid4()))

