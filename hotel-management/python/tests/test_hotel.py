"""End-to-end tests for the Hotel Management MVP, including a date-range race test."""

from __future__ import annotations

import threading
from datetime import date

import pytest

from hotel.exceptions import RoomUnavailableError
from hotel.models import Hotel, ReservationStatus, Room, RoomType
from hotel.service import HotelManagementService
from hotel.strategies import NightlyPricingStrategy


def make_service() -> HotelManagementService:
    hotel = Hotel(
        "Test Hotel",
        [
            Room("101", RoomType.STANDARD),
            Room("102", RoomType.STANDARD),
            Room("201", RoomType.DELUXE),
            Room("301", RoomType.SUITE),
        ],
    )
    return HotelManagementService(hotel, NightlyPricingStrategy())


def test_search_returns_only_rooms_free_for_the_range():
    service = make_service()
    check_in = date(2026, 1, 10)
    check_out = date(2026, 1, 12)

    service.book_room("101", check_in, check_out)

    assert [r.id for r in service.search_available_rooms(RoomType.STANDARD, check_in, check_out)] == ["102"]


def test_adjacent_bookings_are_allowed_but_true_overlaps_are_excluded():
    service = make_service()
    service.book_room("101", date(2026, 1, 10), date(2026, 1, 12))

    adjacent_ids = [
        r.id for r in service.search_available_rooms(RoomType.STANDARD, date(2026, 1, 12), date(2026, 1, 14))
    ]
    overlap_ids = [
        r.id for r in service.search_available_rooms(RoomType.STANDARD, date(2026, 1, 11), date(2026, 1, 13))
    ]
    assert "101" in adjacent_ids
    assert "101" not in overlap_ids


def test_book_computes_total_and_unavailable_room_throws():
    service = make_service()
    check_in = date(2026, 2, 1)
    check_out = date(2026, 2, 4)

    reservation = service.book_room("201", check_in, check_out)  # 3 DELUXE nights * 180
    assert reservation.total_price == 540
    with pytest.raises(RoomUnavailableError):
        service.book_room("201", date(2026, 2, 2), date(2026, 2, 5))


def test_lifecycle_and_cancel_free_dates():
    service = make_service()
    check_in = date(2026, 3, 1)
    check_out = date(2026, 3, 3)

    stay = service.book_room("101", check_in, check_out)
    service.check_in(stay.id)
    assert stay.status is ReservationStatus.CHECKED_IN
    service.check_out(stay.id)
    assert stay.status is ReservationStatus.CHECKED_OUT

    cancelled = service.book_room("102", check_in, check_out)
    service.cancel(cancelled.id)
    assert cancelled.status is ReservationStatus.CANCELLED
    replacement = service.book_room("102", check_in, check_out)
    assert replacement.status is ReservationStatus.CONFIRMED


def test_concurrent_booking_never_double_books_same_room_range():
    service = make_service()
    check_in = date(2026, 4, 1)
    check_out = date(2026, 4, 5)
    threads = 50

    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker() -> None:
        start.wait()  # release all threads together for maximum contention
        try:
            reservation = service.book_room("101", check_in, check_out)
            with successes_lock:
                successes.append(reservation.id)
        except RoomUnavailableError:
            pass  # expected for the losers

    workers = [threading.Thread(target=worker) for _ in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert len(successes) == 1, "exactly one thread may reserve the room"
    assert len(set(successes)) == 1, "only one reservation id is created"
    assert service.active_overlapping_reservations("101", check_in, check_out) == 1
