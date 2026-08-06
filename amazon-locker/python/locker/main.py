"""Runnable demo: build a location, deliver packages, pickup, print status.

Run:  python -m locker.main   (from the python/ directory)
"""

from __future__ import annotations

from .factory import create_package
from .models import PackageSize
from .service import AmazonLockerService, LockerLocation
from .strategies import SmallestFitAssignmentStrategy


def main() -> None:
    location = LockerLocation.of("LOC1", 2, 2, 1)
    service = AmazonLockerService(location, SmallestFitAssignmentStrategy())

    print("Free lockers at open:", service.available_lockers())

    small = create_package("PKG-S", PackageSize.SMALL)
    medium = create_package("PKG-M", PackageSize.MEDIUM)
    large = create_package("PKG-L", PackageSize.LARGE)

    c1 = service.deliver(small)
    c2 = service.deliver(medium)
    c3 = service.deliver(large)
    print("Delivered small package to", service.find_locker(c1).id)
    print("Delivered medium package to", service.find_locker(c2).id)
    print("Delivered large package to", service.find_locker(c3).id)
    print("Free lockers now:", service.available_lockers())

    picked = service.pickup(c2)
    print(f"Picked up package {picked.id}")
    print("Free lockers after pickup:", service.available_lockers())


if __name__ == "__main__":
    main()
