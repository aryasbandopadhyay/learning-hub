"""End-to-end tests for the Parking Lot MVP, including a concurrency race test."""

from __future__ import annotations

import threading
from datetime import datetime, timedelta, timezone

import pytest

from parking_lot.exceptions import InvalidTicketError, NoAvailableSpotError
from parking_lot.factory import create_vehicle
from parking_lot.lot import Level, ParkingLot
from parking_lot.models import SpotType, VehicleType
from parking_lot.strategies import HourlyFeeStrategy, NearestFirstAssignmentStrategy


class MutableClock:
    """Hand-advanced clock so fee tests are deterministic (no sleeps)."""

    def __init__(self, start: datetime) -> None:
        self._now = start

    def __call__(self) -> datetime:
        return self._now

    def advance(self, delta: timedelta) -> None:
        self._now += delta


def make_lot(clock=None) -> ParkingLot:
    # One level: 1 small, 1 medium, 1 large.
    return ParkingLot(
        [Level.of(0, 1, 1, 1)],
        NearestFirstAssignmentStrategy(),
        HourlyFeeStrategy(),
        clock or (lambda: datetime.now(timezone.utc)),
    )


def test_motorcycle_fits_small_spot():
    lot = make_lot()
    assert lot.available_spots() == 3
    ticket = lot.park(create_vehicle(VehicleType.MOTORCYCLE, "M1"))
    assert ticket.spot.spot_type is SpotType.SMALL
    assert lot.available_spots() == 2


def test_truck_only_fits_large_spot():
    lot = make_lot()
    ticket = lot.park(create_vehicle(VehicleType.TRUCK, "T1"))
    assert ticket.spot.spot_type is SpotType.LARGE


def test_raises_when_no_compatible_spot():
    lot = ParkingLot(
        [Level.of(0, 1, 0, 0)],  # only a SMALL spot
        NearestFirstAssignmentStrategy(),
        HourlyFeeStrategy(),
    )
    with pytest.raises(NoAvailableSpotError):
        lot.park(create_vehicle(VehicleType.TRUCK, "T1"))


def test_unpark_computes_fee_and_frees_spot():
    clock = MutableClock(datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc))
    lot = make_lot(clock)
    ticket = lot.park(create_vehicle(VehicleType.CAR, "C1"))  # MEDIUM = 20/hr
    clock.advance(timedelta(minutes=90))  # 1.5h -> 2 hours
    receipt = lot.unpark(ticket.id)
    assert receipt.fee == 2 * 20
    assert lot.available_spots() == 3


def test_minimum_one_hour_charged():
    clock = MutableClock(datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc))
    lot = make_lot(clock)
    ticket = lot.park(create_vehicle(VehicleType.MOTORCYCLE, "M1"))  # SMALL = 10/hr
    clock.advance(timedelta(minutes=5))
    receipt = lot.unpark(ticket.id)
    assert receipt.fee == 10


def test_unpark_twice_is_rejected():
    lot = make_lot()
    ticket = lot.park(create_vehicle(VehicleType.CAR, "C1"))
    lot.unpark(ticket.id)
    with pytest.raises(InvalidTicketError):
        lot.unpark(ticket.id)


def test_concurrent_parking_never_double_allocates():
    medium_spots = 5
    threads = 50
    lot = ParkingLot(
        [Level.of(0, 0, medium_spots, 0)],
        NearestFirstAssignmentStrategy(),
        HourlyFeeStrategy(),
    )

    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()  # release all threads together for maximum contention
        try:
            ticket = lot.park(create_vehicle(VehicleType.CAR, f"C{i}"))
            with successes_lock:
                successes.append(ticket.spot.id)
        except NoAvailableSpotError:
            pass  # expected for the losers

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert len(successes) == medium_spots, "exactly capacity should park"
    assert len(set(successes)) == medium_spots, "no spot id claimed twice"
    assert lot.available_spots() == 0
