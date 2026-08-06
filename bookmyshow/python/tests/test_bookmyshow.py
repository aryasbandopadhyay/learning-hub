"""End-to-end tests for BookMyShow, with the seat-locking race as centerpiece."""

from __future__ import annotations

import threading
from datetime import datetime, timedelta, timezone

import pytest

from bookmyshow.exceptions import HoldExpiredError, SeatUnavailableError
from bookmyshow.models import City, Movie, Screen, Seat, SeatStatus, Show, Theater
from bookmyshow.service import BookMyShowService


class MutableClock:
    """Hand-advanced clock so hold-expiry tests are deterministic (no sleeps)."""

    def __init__(self, start: datetime) -> None:
        self._now = start

    def __call__(self) -> datetime:
        return self._now

    def advance(self, delta: timedelta) -> None:
        self._now += delta


def make_service(clock=None) -> BookMyShowService:
    service = BookMyShowService(clock or (lambda: datetime.now(timezone.utc)), timedelta(minutes=5))
    service.add_city(sample_city())
    return service


def test_search_returns_shows_for_movie_in_city():
    service = make_service()
    shows = service.search_shows("Bengaluru", "Interstellar")
    assert len(shows) == 2
    assert all(show.movie.title == "Interstellar" for show in shows)


def test_hold_then_confirm_books_seats_and_subsequent_hold_fails():
    service = make_service()
    hold = service.hold_seats("show-1", ["A1", "A2"], "user-1")
    booking = service.confirm_booking(hold.id, "pay-1")
    assert booking.id
    assert service.seat_status("show-1", "A1") is SeatStatus.BOOKED
    with pytest.raises(SeatUnavailableError):
        service.hold_seats("show-1", ["A1"], "user-2")


def test_all_or_nothing_hold_failure_leaves_available_seat_available():
    service = make_service()
    service.hold_seats("show-1", ["A1"], "user-1")
    with pytest.raises(SeatUnavailableError):
        service.hold_seats("show-1", ["A2", "A1"], "user-2")
    assert service.seat_status("show-1", "A2") is SeatStatus.AVAILABLE


def test_expired_hold_cannot_confirm_and_releases_seats():
    clock = MutableClock(datetime(2026, 8, 4, 10, tzinfo=timezone.utc))
    service = make_service(clock)
    hold = service.hold_seats("show-1", ["A1"], "user-1")
    clock.advance(timedelta(minutes=6))
    with pytest.raises(HoldExpiredError):
        service.confirm_booking(hold.id, "pay-late")
    assert service.seat_status("show-1", "A1") is SeatStatus.AVAILABLE


def test_release_expired_holds_makes_seats_available_again():
    clock = MutableClock(datetime(2026, 8, 4, 10, tzinfo=timezone.utc))
    service = make_service(clock)
    service.hold_seats("show-1", ["A1"], "user-1")
    clock.advance(timedelta(minutes=6))
    service.release_expired_holds(clock())
    assert service.seat_status("show-1", "A1") is SeatStatus.AVAILABLE


def test_concurrent_holds_for_same_seat_exactly_one_succeeds():
    threads = 50
    service = make_service()
    start = threading.Event()
    winners: list[str] = []
    winners_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()
        try:
            hold = service.hold_seats("show-1", ["A1"], f"user-{i}")
            with winners_lock:
                winners.append(hold.user_id)
        except SeatUnavailableError:
            pass

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert len(winners) == 1, "exactly one user can hold the seat"
    assert len(set(winners)) == 1, "there is only one winner identity"
    assert service.seat_status("show-1", "A1") is SeatStatus.HELD


def test_concurrent_holds_for_distinct_seats_all_succeed_without_overlap():
    seat_count = 5
    service = make_service()
    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker(seat_id: str) -> None:
        start.wait()
        service.hold_seats("show-1", [seat_id], f"user-{seat_id}")
        with successes_lock:
            successes.append(seat_id)

    workers = [threading.Thread(target=worker, args=(f"A{i}",)) for i in range(1, seat_count + 1)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert len(successes) == seat_count
    assert len(set(successes)) == seat_count
    for i in range(1, seat_count + 1):
        assert service.seat_status("show-1", f"A{i}") is SeatStatus.HELD


def sample_city() -> City:
    interstellar = Movie("movie-1", "Interstellar")
    matrix = Movie("movie-2", "Matrix")
    screen1 = Screen("screen-1", "Audi 1")
    screen1.add_show(Show("show-1", interstellar, datetime(2026, 8, 5, 18, tzinfo=timezone.utc), seats("A", 5)))
    screen1.add_show(Show("show-2", matrix, datetime(2026, 8, 5, 21, tzinfo=timezone.utc), seats("B", 5)))
    theater1 = Theater("theater-1", "PVR Orion")
    theater1.add_screen(screen1)

    screen2 = Screen("screen-2", "Audi 2")
    screen2.add_show(Show("show-3", interstellar, datetime(2026, 8, 5, 20, tzinfo=timezone.utc), seats("C", 5)))
    theater2 = Theater("theater-2", "INOX Garuda")
    theater2.add_screen(screen2)

    city = City("city-1", "Bengaluru")
    city.add_theater(theater1)
    city.add_theater(theater2)
    return city


def seats(row: str, count: int) -> list[Seat]:
    return [Seat(row, i) for i in range(1, count + 1)]

