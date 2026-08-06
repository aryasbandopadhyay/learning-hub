"""Thread-safe generic in-memory cache with pluggable eviction."""

from __future__ import annotations

import threading
from typing import Generic, Hashable, TypeVar

from .exceptions import InvalidCapacityError
from .policies import EvictionPolicy

K = TypeVar("K", bound=Hashable)
V = TypeVar("V")
_MISSING = object()


class Cache(Generic[K, V]):
    """Cache service using Strategy for eviction and an RLock for concurrency.

    A single lock protects both the value dict and the policy metadata. That coarse-grained model is
    deliberate for a machine-coding MVP: every get/put is atomic, policy state cannot drift away from
    stored values, and size never exceeds capacity under concurrent access.
    """

    def __init__(self, capacity: int, eviction_policy: EvictionPolicy[K]) -> None:
        if capacity <= 0:
            raise InvalidCapacityError("Capacity must be positive")
        self._capacity = capacity
        self._policy = eviction_policy
        self._values: dict[K, V] = {}
        self._lock = threading.RLock()

    def get(self, key: K) -> V | None:
        """Return the value or None on miss; hits update recency/frequency."""
        with self._lock:
            value = self._values.get(key, _MISSING)
            if value is _MISSING:
                return None
            self._policy.key_accessed(key)
            return value  # type: ignore[return-value]

    def put(self, key: K, value: V) -> None:
        """Insert/update a value, evicting through the policy when full."""
        with self._lock:
            if key in self._values:
                self._values[key] = value
                self._policy.key_accessed(key)
                return
            if len(self._values) >= self._capacity:
                victim = self._policy.evict_key()
                if victim is not None:
                    self._values.pop(victim, None)
            self._values[key] = value
            self._policy.key_inserted(key)

    def size(self) -> int:
        with self._lock:
            return len(self._values)

    @property
    def capacity(self) -> int:
        return self._capacity

    def contains_key(self, key: K) -> bool:
        """Membership check does not count as an access; only get/put alter eviction state."""
        with self._lock:
            return key in self._values
