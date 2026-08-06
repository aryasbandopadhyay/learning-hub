"""End-to-end tests for the Car Rental MVP, including a concurrency race test."""

from __future__ import annotations

import threading
from datetime import date

import pytest

from carrental.exceptions import CarUnavailableError
from carrental.models import Car, CarType, ReservationStatus
from carrental.service import RentalCompany
from carrental.strategies import DailyRatePricingStrategy, PricingStrategy


SEP_1 = date(2026, 9, 1)
SEP_4 = date(2026, 9, 4)


def make_company(strategy: PricingStrategy | None = None) -> RentalCompany:
    return RentalCompany(
        [
            Car("E1", "KA-01-1111", CarType.ECONOMY, "BLR"),
            Car("S1", "KA-02-2222", CarType.SUV, "BLR"),
            Car("S2", "KA-03-3333", CarType.SUV, "BLR"),
            Car("L1", "DL-04-4444", CarType.LUXURY, "DEL"),
        ],
        strategy or DailyRatePricingStrategy(),
    )


def test_search_excludes_overlapping_reservations_but_allows_adjacent_ranges():
    company = make_company()
    company.reserve("S1", SEP_1, SEP_4)

    overlapping = company.search_available("BLR", CarType.SUV, date(2026, 9, 3), date(2026, 9, 5))
    assert [car.id for car in overlapping] == ["S2"]

    adjacent = company.search_available("BLR", CarType.SUV, SEP_4, date(2026, 9, 6))
    assert len(adjacent) == 2  # return day is free for the next pickup


def test_reserve_computes_total_and_rejects_unavailable_car():
    company = make_company()
    reservation = company.reserve("S1", SEP_1, SEP_4)

    assert reservation.total_price == 3 * CarType.SUV.daily_rate
    assert reservation.status is ReservationStatus.CONFIRMED
    with pytest.raises(CarUnavailableError):
        company.reserve("S1", date(2026, 9, 2), SEP_4)


def test_lifecycle_and_cancel_frees_the_range():
    company = make_company()
    reservation = company.reserve("E1", SEP_1, SEP_4)

    company.pick_up(reservation.id)
    assert reservation.status is ReservationStatus.PICKED_UP
    company.return_car(reservation.id)
    assert reservation.status is ReservationStatus.RETURNED

    cancelled = company.reserve("S1", SEP_1, SEP_4)
    company.cancel(cancelled.id)
    assert cancelled.status is ReservationStatus.CANCELLED
    assert company.reserve("S1", SEP_1, SEP_4).car.id == "S1"


def test_pricing_strategy_is_swappable():
    class FlatPricing(PricingStrategy):
        def calculate_price(self, car: Car, pickup_date: date, return_date: date) -> int:
            return 999

    default_company = make_company()
    custom_company = make_company(FlatPricing())

    assert default_company.reserve("L1", SEP_1, SEP_4).total_price == 3 * CarType.LUXURY.daily_rate
    assert custom_company.reserve("L1", SEP_1, SEP_4).total_price == 999


def test_concurrent_reserve_never_double_books_same_car_for_overlapping_dates():
    threads = 50
    company = RentalCompany(
        [Car("S1", "KA-02-2222", CarType.SUV, "BLR")],
        DailyRatePricingStrategy(),
    )
    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker() -> None:
        start.wait()  # release all threads together for maximum contention
        try:
            reservation = company.reserve("S1", SEP_1, SEP_4)
            with successes_lock:
                successes.append(reservation.id)
        except CarUnavailableError:
            pass  # expected for the losers

    workers = [threading.Thread(target=worker) for _ in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    blocking = [r for r in company.reservations_for_car("S1") if r.blocks_availability()]
    assert len(successes) == 1
    assert len(set(successes)) == 1
    assert len(blocking) == 1, "no overlapping confirmed/picked-up reservations exist"
