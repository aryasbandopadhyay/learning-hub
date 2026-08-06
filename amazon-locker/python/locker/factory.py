"""Factory pattern: centralized creation of packages and lockers."""

from __future__ import annotations

from .models import DeliveryPackage, Locker, LockerSize, PackageSize


def create_package(package_id: str, size: PackageSize) -> DeliveryPackage:
    """Build a package. Callers depend on simple values, not concrete constructor details."""
    return DeliveryPackage(package_id, size)


def create_locker(locker_id: str, size: LockerSize) -> Locker:
    """Build a locker with a known id and size."""
    return Locker(locker_id, size)
