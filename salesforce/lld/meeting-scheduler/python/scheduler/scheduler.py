"""The orchestrating service plus the Meeting Rooms II utility."""

from __future__ import annotations

import threading
from datetime import date
from typing import Sequence

from .models import Attendee, Booking, MeetingRoom, TimeInterval
from .strategies import RoomSelectionStrategy


class NoAvailableRoomError(RuntimeError):
    """Raised when every room conflicts with the requested interval."""


class BookingNotFoundError(RuntimeError):
    """Raised when cancellation references an unknown/already-cancelled booking."""


class MeetingScheduler:
    """Aggregate root wiring room calendars + selection strategy.

    Depends on the RoomSelectionStrategy abstraction, so placement policy can change without edits
    here. A small lock guards the active-bookings dict; each room guards its own calendar.
    """

    def __init__(self, rooms: Sequence[MeetingRoom], room_selection_strategy: RoomSelectionStrategy) -> None:
        self._rooms: tuple[MeetingRoom, ...] = tuple(rooms)
        self._strategy = room_selection_strategy
        self._active_bookings: dict[str, Booking] = {}
        self._lock = threading.Lock()

    def book(self, title: str, interval: TimeInterval, attendees: Sequence[Attendee]) -> Booking:
        booking = self._strategy.book(self._rooms, interval, title, attendees)
        if booking is None:
            raise NoAvailableRoomError(f"No available room for interval {interval.display()}")
        with self._lock:
            self._active_bookings[booking.id] = booking
        return booking

    def cancel(self, booking_id: str) -> None:
        with self._lock:
            booking = self._active_bookings.pop(booking_id, None)
        if booking is None:
            raise BookingNotFoundError(f"Unknown or already-cancelled booking: {booking_id}")
        booking.room.cancel(booking_id)

    def list_bookings_for_room_day(self, room_id: str, day: date) -> list[Booking]:
        for room in self._rooms:
            if room.id == room_id:
                return room.bookings_for_day(day)
        raise ValueError(f"Unknown room: {room_id}")

    @property
    def rooms(self) -> tuple[MeetingRoom, ...]:
        return self._rooms


def minimum_rooms_required(meetings: Sequence[TimeInterval]) -> int:
    """Classic sweep-line utility for half-open intervals [start, end)."""
    if not meetings:
        return 0

    starts = sorted(m.start for m in meetings)
    ends = sorted(m.end for m in meetings)
    active = 0
    max_active = 0
    end_index = 0

    for start in starts:
        while end_index < len(ends) and start >= ends[end_index]:
            active -= 1
            end_index += 1
        active += 1
        max_active = max(max_active, active)
    return max_active
