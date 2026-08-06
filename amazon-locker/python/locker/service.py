"""The orchestrating service: LockerLocation and AmazonLockerService."""

from __future__ import annotations

import random
import threading
from typing import Optional, Sequence

from .exceptions import InvalidPickupCodeError, NoAvailableLockerError
from .factory import create_locker
from .models import DeliveryPackage, Locker, LockerSize
from .strategies import LockerAssignmentStrategy


class LockerLocation:
    """A physical location holding a fixed list of lockers. Concurrency is handled per-locker."""

    def __init__(self, location_id: str, lockers: Sequence[Locker]) -> None:
        self.id = location_id
        self.lockers: tuple[Locker, ...] = tuple(lockers)

    @classmethod
    def of(cls, location_id: str, small: int, medium: int, large: int) -> "LockerLocation":
        lockers: list[Locker] = []
        idx = 0
        for _ in range(small):
            lockers.append(create_locker(f"{location_id}-L{idx}", LockerSize.SMALL))
            idx += 1
        for _ in range(medium):
            lockers.append(create_locker(f"{location_id}-L{idx}", LockerSize.MEDIUM))
            idx += 1
        for _ in range(large):
            lockers.append(create_locker(f"{location_id}-L{idx}", LockerSize.LARGE))
            idx += 1
        return cls(location_id, lockers)

    def available_count(self) -> int:
        return sum(1 for locker in self.lockers if locker.is_free)


class AmazonLockerService:
    """Aggregate root wiring model + strategy.

    Allocation is not protected by a coarse service lock; each locker owns its atomic ``try_occupy``.
    A small lock guards the code->locker dict so pickup codes are issued and consumed safely.
    """

    def __init__(self, location: LockerLocation, assignment_strategy: LockerAssignmentStrategy) -> None:
        self._location = location
        self._assignment = assignment_strategy
        self._code_to_locker: dict[str, Locker] = {}
        self._codes_lock = threading.Lock()
        self._random = random.SystemRandom()

    def deliver(self, package: DeliveryPackage) -> str:
        """Reserve the smallest fitting locker and return a random six-digit pickup code."""
        locker = self._assignment.assign(self._location, package)
        if locker is None:
            raise NoAvailableLockerError(f"No available locker for {package!r}")
        with self._codes_lock:
            code = self._new_pickup_code_locked()
            self._code_to_locker[code] = locker
        return code

    def pickup(self, code: str) -> DeliveryPackage:
        """Consume the code, open/free the locker, and return the package."""
        with self._codes_lock:
            locker = self._code_to_locker.pop(code, None)
        if locker is None:
            raise InvalidPickupCodeError(f"Unknown or already-used pickup code: {code}")
        package = locker.free()
        assert package is not None  # invariant: a code only points at an occupied locker
        return package

    def _new_pickup_code_locked(self) -> str:
        while True:
            code = f"{self._random.randrange(1_000_000):06d}"
            if code not in self._code_to_locker:
                return code

    def available_lockers(self) -> int:
        return self._location.available_count()

    def find_locker(self, code: str) -> Optional[Locker]:
        with self._codes_lock:
            return self._code_to_locker.get(code)

    @property
    def location(self) -> LockerLocation:
        return self._location
