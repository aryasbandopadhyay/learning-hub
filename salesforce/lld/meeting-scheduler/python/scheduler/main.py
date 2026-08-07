"""Runnable demo with deterministic data/output for quick manual verification."""

from __future__ import annotations

from datetime import date, datetime

from .models import Attendee, MeetingRoom, TimeInterval
from .scheduler import MeetingScheduler, minimum_rooms_required
from .strategies import FirstAvailableRoomSelectionStrategy


def interval(sh: int, sm: int, eh: int, em: int) -> TimeInterval:
    return TimeInterval(datetime(2024, 1, 1, sh, sm), datetime(2024, 1, 1, eh, em))


def main() -> None:
    rooms = [
        MeetingRoom("R1", "Room-A", 4),
        MeetingRoom("R2", "Room-B", 6),
        MeetingRoom("R3", "Room-C", 8),
    ]
    scheduler = MeetingScheduler(rooms, FirstAvailableRoomSelectionStrategy())

    planning = interval(9, 0, 10, 0)
    standup = interval(9, 30, 10, 0)
    attendees = [Attendee("alice@example.com"), Attendee("bob@example.com")]

    b1 = scheduler.book("Planning", planning, attendees)
    b2 = scheduler.book("Standup", standup, attendees)

    print(f"Rooms at open: {len(rooms)}")
    print(f"Booked Planning in {b1.room.name} {b1.interval.display()}")
    print(f"Booked Standup  in {b2.room.name} {b2.interval.display()}")
    print(f"Bookings in Room-A on 2024-01-01: {len(scheduler.list_bookings_for_room_day('R1', date(2024, 1, 1)))}")
    print(f"Minimum rooms needed for sample: {minimum_rooms_required([planning, standup, interval(10, 0, 11, 0)])}")
    scheduler.cancel(b1.id)
    print(f"Cancelled Planning; Room-A bookings now: {len(scheduler.list_bookings_for_room_day('R1', date(2024, 1, 1)))}")


if __name__ == "__main__":
    main()
