"""Domain models: attendees, half-open intervals, bookings, and room calendars."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import date, datetime


@dataclass(frozen=True)
class Attendee:
    """Simple value object for a participant. Invites/persistence are out of MVP scope."""

    email: str

    def __post_init__(self) -> None:
        if not self.email:
            raise ValueError("email must not be blank")


@dataclass(frozen=True)
class TimeInterval:
    """Half-open interval [start, end), so back-to-back meetings do not conflict."""

    start: datetime
    end: datetime

    def __post_init__(self) -> None:
        if self.start >= self.end:
            raise ValueError("start must be before end")

    def overlaps(self, other: "TimeInterval") -> bool:
        return self.start < other.end and other.start < self.end

    def starts_on(self, day: date) -> bool:
        return self.start.date() == day

    def display(self) -> str:
        return f"[{self.start.isoformat(timespec='minutes')}, {self.end.isoformat(timespec='minutes')})"


@dataclass(frozen=True)
class Booking:
    """Immutable booking returned after a room atomically accepts the interval."""

    title: str
    room: "MeetingRoom"
    interval: TimeInterval
    attendees: tuple[Attendee, ...]
    id: str = field(default_factory=lambda: str(uuid.uuid4()))


class MeetingRoom:
    """A room owns its calendar. THIS CLASS IS THE CONCURRENCY BOUNDARY.

    ``try_book`` and ``cancel`` hold ``self._lock`` so the overlap scan and insert/remove are one
    atomic step. Multiple rooms can still be booked in parallel, but one room cannot be double-booked.
    """

    def __init__(self, room_id: str, name: str, capacity: int) -> None:
        self.id = room_id
        self.name = name
        self.capacity = capacity
        self._bookings: list[Booking] = []
        self._successful_bookings = 0
        self._lock = threading.Lock()

    def try_book(self, candidate: Booking) -> bool:
        """Atomically add the booking only if no existing booking overlaps it."""
        with self._lock:
            if any(b.interval.overlaps(candidate.interval) for b in self._bookings):
                return False
            self._bookings.append(candidate)
            self._successful_bookings += 1
            return True

    def cancel(self, booking_id: str) -> bool:
        with self._lock:
            before = len(self._bookings)
            self._bookings = [b for b in self._bookings if b.id != booking_id]
            return len(self._bookings) != before

    def bookings_for_day(self, day: date) -> list[Booking]:
        with self._lock:
            return sorted((b for b in self._bookings if b.interval.starts_on(day)), key=lambda b: b.interval.start)

    def booking_count(self) -> int:
        with self._lock:
            return self._successful_bookings
