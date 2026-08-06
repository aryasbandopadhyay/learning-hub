"""End-to-end tests for the Airline Reservation MVP, including a concurrency race test."""

from __future__ import annotations

import threading
from datetime import date, datetime, timezone

import pytest

from airline.exceptions import BookingNotFoundError, NoSeatAvailableError, SeatAlreadyBookedError
from airline.models import Booking, Cabin, Flight, FlightInventory, Passenger, Seat, SeatStatus
from airline.service import AirlineReservationService
from airline.strategies import FixedCabinPricingStrategy


def flight(no: str, origin: str, dest: str, departure: datetime, economy: int, business: int) -> Flight:
    seats: list[Seat] = []
    for i in range(business):
        seats.append(Seat(f"1{chr(ord('A') + i)}", Cabin.BUSINESS))
    for i in range(economy):
        seats.append(Seat(f"12{chr(ord('A') + i)}", Cabin.ECONOMY))
    return Flight(no, origin, dest, departure, FlightInventory(seats))


def new_service() -> AirlineReservationService:
    service = AirlineReservationService(
        FixedCabinPricingStrategy(),
        clock=lambda: datetime(2026, 8, 4, 10, 0, tzinfo=timezone.utc),
    )
    service.add_flight(flight("AI101", "BLR", "DEL", datetime(2026, 8, 5, 9, 30), 2, 2))
    service.add_flight(flight("AI202", "BLR", "DEL", datetime(2026, 8, 6, 11, 0), 1, 0))
    service.add_flight(flight("AI303", "DEL", "BOM", datetime(2026, 8, 5, 18, 0), 1, 1))
    return service


def test_search_returns_route_and_can_filter_by_date_and_availability():
    service = new_service()
    assert len(service.search_flights("BLR", "DEL")) == 2
    assert service.search_flights("BLR", "DEL", date(2026, 8, 5), True)[0].flight_number == "AI101"

    service.book_any("AI202", Cabin.ECONOMY, Passenger("Only", "only@example.com"))
    assert service.search_flights("BLR", "DEL", date(2026, 8, 6), True) == []
    assert service.search_flights("BLR", "DEL", date(2026, 8, 6), False) != []


def test_book_specific_seat_creates_pnr_and_rejects_second_booking():
    service = new_service()
    booking = service.book_seat("AI101", "1A", Passenger("Alice", "a@example.com"))

    assert booking.pnr.startswith("PNR-")
    assert booking.seat_no == "1A"
    assert service.search_flights("BLR", "DEL")[0].inventory.find_seat("1A").status is SeatStatus.BOOKED
    with pytest.raises(SeatAlreadyBookedError):
        service.book_seat("AI101", "1A", Passenger("Bob", "b@example.com"))


def test_book_any_picks_requested_cabin_and_rejects_when_cabin_full():
    service = new_service()
    first = service.book_any("AI101", Cabin.BUSINESS, Passenger("Biz1", "b1@example.com"))
    second = service.book_any("AI101", Cabin.BUSINESS, Passenger("Biz2", "b2@example.com"))

    assert first.cabin is Cabin.BUSINESS
    assert second.cabin is Cabin.BUSINESS
    with pytest.raises(NoSeatAvailableError):
        service.book_any("AI101", Cabin.BUSINESS, Passenger("Biz3", "b3@example.com"))


def test_cancel_frees_seat_and_pnr_cannot_be_cancelled_twice():
    service = new_service()
    booking = service.book_seat("AI101", "12A", Passenger("Alice", "a@example.com"))

    service.cancel(booking.pnr)
    assert service.search_flights("BLR", "DEL")[0].inventory.find_seat("12A").is_available
    rebooked = service.book_seat("AI101", "12A", Passenger("Bob", "b@example.com"))
    assert rebooked.seat_no == "12A"
    with pytest.raises(BookingNotFoundError):
        service.cancel(booking.pnr)


def test_concurrent_specific_seat_booking_never_double_books():
    service = AirlineReservationService()
    service.add_flight(flight("AI999", "BLR", "DEL", datetime(2026, 8, 5, 9, 30), 1, 0))

    threads = 50
    start = threading.Event()
    successes: list[Booking] = []
    successes_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()  # release all threads together for maximum contention
        try:
            booking = service.book_seat("AI999", "12A", Passenger(f"P{i}", f"p{i}@example.com"))
            with successes_lock:
                successes.append(booking)
        except SeatAlreadyBookedError:
            pass  # expected for the losers

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert len(successes) == 1, "only one passenger can own seat 12A"
    assert len({b.pnr for b in successes}) == 1, "only one unique PNR should exist"
    assert service.search_flights("BLR", "DEL")[0].inventory.available_count() == 0
