"""Strategy pattern: pluggable locker assignment policies."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

from .models import DeliveryPackage, Locker

if TYPE_CHECKING:  # avoid a runtime import cycle (service imports strategies)
    from .service import LockerLocation


class LockerAssignmentStrategy(ABC):
    """Decides WHICH locker a package gets, and atomically reserves it."""

    @abstractmethod
    def assign(self, location: "LockerLocation", package: DeliveryPackage) -> Locker | None:
        ...


class SmallestFitAssignmentStrategy(LockerAssignmentStrategy):
    """Try SMALL lockers first, then MEDIUM, then LARGE, preserving bigger lockers."""

    def assign(self, location: "LockerLocation", package: DeliveryPackage) -> Locker | None:
        for locker in sorted(location.lockers, key=lambda l: (l.size, l.id)):
            if locker.try_occupy(package):
                return locker
        return None
