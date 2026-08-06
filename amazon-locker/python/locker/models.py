"""Domain models: package/locker sizes, locker state, package, and locker."""

from __future__ import annotations

import threading
from dataclasses import dataclass
from enum import Enum, IntEnum


class PackageSize(IntEnum):
    """Package size. IntEnum so ``locker.size >= package.size`` models the fit rule."""

    SMALL = 0
    MEDIUM = 1
    LARGE = 2


class LockerSize(IntEnum):
    """Physical locker size, ordered from smallest to largest."""

    SMALL = 0
    MEDIUM = 1
    LARGE = 2


class LockerState(Enum):
    FREE = "FREE"
    OCCUPIED = "OCCUPIED"


@dataclass(frozen=True)
class DeliveryPackage:
    """Immutable package request. Real systems would also include customer/order metadata."""

    id: str
    size: PackageSize


class Locker:
    """A single locker. THE CONCURRENCY BOUNDARY.

    ``try_occupy`` and ``free`` hold ``self._lock`` so the "free and fits?" check and the state
    change are one atomic step. When many threads race for the last locker, exactly one wins.
    """

    def __init__(self, locker_id: str, size: LockerSize) -> None:
        self.id = locker_id
        self.size = size
        self._state = LockerState.FREE
        self._package: DeliveryPackage | None = None
        self._lock = threading.Lock()

    def can_fit(self, package: DeliveryPackage) -> bool:
        return self.size >= package.size

    def try_occupy(self, package: DeliveryPackage) -> bool:
        """Atomically claim this locker; return True on success."""
        with self._lock:
            if self._state is not LockerState.FREE or not self.can_fit(package):
                return False
            self._state = LockerState.OCCUPIED
            self._package = package
            return True

    def free(self) -> DeliveryPackage | None:
        with self._lock:
            package = self._package
            self._package = None
            self._state = LockerState.FREE
            return package

    @property
    def is_free(self) -> bool:
        with self._lock:
            return self._state is LockerState.FREE

    @property
    def state(self) -> LockerState:
        with self._lock:
            return self._state
