"""End-to-end tests for the Meeting Scheduler MVP, including a concurrency race test."""

from __future__ import annotations

import threading
from datetime import date, datetime

import pytest

from scheduler.models import Attendee, MeetingRoom, TimeInterval
from scheduler.scheduler import MeetingScheduler, NoAvailableRoomError, minimum_rooms_required
from scheduler.strategies import FirstAvailableRoomSelectionStrategy


def interval(sh: int, sm: int, eh: int, em: int) -> TimeInterval:
    return TimeInterval(datetime(2024, 1, 1, sh, sm), datetime(2024, 1, 1, eh, em))


def make_scheduler(room_count: int) -> MeetingScheduler:
    return MeetingScheduler(
        [MeetingRoom(f"R{i}", f"Room-{i}", 4) for i in range(1, room_count + 1)],
        FirstAvailableRoomSelectionStrategy(),
    )


def test_booking_success_allocates_first_free_room():
    scheduler = make_scheduler(2)
    booking = scheduler.book("Planning", interval(9, 0, 10, 0), [Attendee("a@example.com")])

    assert booking.room.id == "R1"
    assert len(scheduler.list_bookings_for_room_day("R1", date(2024, 1, 1))) == 1


def test_overlap_rejected_when_no_room_is_free():
    scheduler = make_scheduler(1)
    scheduler.book("Planning", interval(9, 0, 10, 0), [Attendee("a@example.com")])

    with pytest.raises(NoAvailableRoomError):
        scheduler.book("Conflict", interval(9, 30, 10, 30), [Attendee("b@example.com")])


def test_cancel_frees_room_for_same_interval():
    scheduler = make_scheduler(1)
    booking = scheduler.book("Planning", interval(9, 0, 10, 0), [Attendee("a@example.com")])
    scheduler.cancel(booking.id)

    replacement = scheduler.book("Replacement", interval(9, 0, 10, 0), [Attendee("b@example.com")])
    assert replacement.room.id == "R1"
    assert len(scheduler.list_bookings_for_room_day("R1", date(2024, 1, 1))) == 1


def test_minimum_rooms_utility_uses_half_open_sweep_line():
    meetings = [interval(9, 0, 10, 0), interval(9, 30, 11, 0), interval(10, 0, 10, 30), interval(11, 0, 12, 0)]

    assert minimum_rooms_required(meetings) == 2
    assert minimum_rooms_required([]) == 0


def test_concurrent_booking_never_double_books_same_room():
    rooms = 5
    threads = 50
    scheduler = make_scheduler(rooms)
    slot = interval(9, 0, 10, 0)

    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()  # release all threads together for maximum contention
        try:
            booking = scheduler.book("Race" + str(i), slot, [Attendee(f"u{i}@example.com")])
            with successes_lock:
                successes.append(booking.room.id)
        except NoAvailableRoomError:
            pass  # expected for the losers

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert len(successes) == rooms, "exactly one booking per room should succeed"
    assert len(set(successes)) == rooms, "no room may be double-booked"
