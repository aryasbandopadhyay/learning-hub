"""End-to-end tests for the Amazon Locker MVP, including a concurrency race test."""

from __future__ import annotations

import threading

import pytest

from locker.exceptions import InvalidPickupCodeError, NoAvailableLockerError
from locker.factory import create_package
from locker.models import LockerSize, PackageSize
from locker.service import AmazonLockerService, LockerLocation
from locker.strategies import SmallestFitAssignmentStrategy


def make_service(small: int = 1, medium: int = 1, large: int = 1) -> AmazonLockerService:
    return AmazonLockerService(
        LockerLocation.of("LOC1", small, medium, large),
        SmallestFitAssignmentStrategy(),
    )


def test_smallest_fit_uses_small_then_medium_when_small_is_full():
    service = make_service(1, 1, 0)
    first = service.deliver(create_package("S1", PackageSize.SMALL))
    assert service.find_locker(first).size is LockerSize.SMALL

    second = service.deliver(create_package("S2", PackageSize.SMALL))
    assert service.find_locker(second).size is LockerSize.MEDIUM


def test_large_package_cannot_fit_small_locker():
    service = make_service(1, 0, 0)
    with pytest.raises(NoAvailableLockerError):
        service.deliver(create_package("L1", PackageSize.LARGE))


def test_deliver_then_pickup_returns_same_package_and_frees_locker():
    service = make_service(1, 0, 0)
    package = create_package("S1", PackageSize.SMALL)
    code = service.deliver(package)
    assert service.available_lockers() == 0

    picked = service.pickup(code)
    assert picked == package
    assert service.available_lockers() == 1


def test_invalid_pickup_code_is_rejected():
    service = make_service(1, 0, 0)
    with pytest.raises(InvalidPickupCodeError):
        service.pickup("000000")


def test_already_used_pickup_code_is_rejected():
    service = make_service(1, 0, 0)
    code = service.deliver(create_package("S1", PackageSize.SMALL))
    service.pickup(code)
    with pytest.raises(InvalidPickupCodeError):
        service.pickup(code)


def test_full_location_throws_no_available_locker():
    service = make_service(1, 0, 0)
    service.deliver(create_package("S1", PackageSize.SMALL))
    with pytest.raises(NoAvailableLockerError):
        service.deliver(create_package("S2", PackageSize.SMALL))


def test_concurrent_delivery_never_double_allocates():
    medium_lockers = 5
    threads = 50
    service = make_service(0, medium_lockers, 0)

    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()  # release all threads together for maximum contention
        try:
            code = service.deliver(create_package(f"M{i}", PackageSize.MEDIUM))
            with successes_lock:
                successes.append(service.find_locker(code).id)
        except NoAvailableLockerError:
            pass  # expected for the losers

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert len(successes) == medium_lockers, "exactly capacity should deliver"
    assert len(set(successes)) == medium_lockers, "no locker id claimed twice"
    assert service.available_lockers() == 0
