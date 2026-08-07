"""Strategy pattern: pluggable room-selection policies."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Optional, Sequence

from .models import Attendee, Booking, MeetingRoom, TimeInterval


class RoomSelectionStrategy(ABC):
    """Decides WHICH room gets a meeting, and atomically reserves it."""

    @abstractmethod
    def book(self, rooms: Sequence[MeetingRoom], interval: TimeInterval, title: str, attendees: Sequence[Attendee]) -> Optional[Booking]:
        ...


class FirstAvailableRoomSelectionStrategy(RoomSelectionStrategy):
    """Scan rooms by id and claim the first calendar that accepts the interval."""

    def book(self, rooms: Sequence[MeetingRoom], interval: TimeInterval, title: str, attendees: Sequence[Attendee]) -> Optional[Booking]:
        for room in sorted(rooms, key=lambda r: r.id):
            candidate = Booking(title, room, interval, tuple(attendees))
            if room.try_book(candidate):
                return candidate
        return None


class LeastUsedRoomSelectionStrategy(RoomSelectionStrategy):
    """Pick the room with the fewest successful bookings to keep usage balanced."""

    def book(self, rooms: Sequence[MeetingRoom], interval: TimeInterval, title: str, attendees: Sequence[Attendee]) -> Optional[Booking]:
        for room in sorted(rooms, key=lambda r: (r.booking_count(), r.id)):
            candidate = Booking(title, room, interval, tuple(attendees))
            if room.try_book(candidate):
                return candidate
        return None
