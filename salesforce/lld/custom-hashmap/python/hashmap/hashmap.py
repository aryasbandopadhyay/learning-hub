"""A from-scratch generic hash map using separate chaining and dynamic resizing.

This module deliberately does not store entries in Python's ``dict``. The backing store is a list of
bucket heads. Each bucket is a linked list of ``_Node`` objects, so colliding keys remain reachable
by walking the chain. Once ``size / capacity`` exceeds the load factor, capacity doubles and every
node is re-bucketed.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import Generic, Iterator, Optional, TypeVar

K = TypeVar("K")
V = TypeVar("V")


@dataclass
class _Node(Generic[K, V]):
    """One entry in a bucket chain."""

    key: K
    value: V
    next: Optional["_Node[K, V]"] = None


class MyHashMap(Generic[K, V]):
    """Hash map MVP: put/get/remove/contains_key/size with chaining + resizing."""

    DEFAULT_INITIAL_CAPACITY = 16
    DEFAULT_LOAD_FACTOR = 0.75

    def __init__(self, initial_capacity: int = DEFAULT_INITIAL_CAPACITY, load_factor: float = DEFAULT_LOAD_FACTOR) -> None:
        if initial_capacity <= 0:
            raise ValueError("initial_capacity must be positive")
        if load_factor <= 0:
            raise ValueError("load_factor must be positive")
        self._buckets: list[_Node[K, V] | None] = [None] * self._next_power_of_two(initial_capacity)
        self._load_factor = load_factor
        self._size = 0

    def put(self, key: K, value: V) -> V | None:
        """Insert or overwrite a key. Return the old value, or None when the key was new."""
        index = self._index_for(key, len(self._buckets))
        current = self._buckets[index]
        while current is not None:
            if current.key == key:
                old = current.value
                current.value = value
                return old
            current = current.next

        self._ensure_capacity_for(self._size + 1)
        index = self._index_for(key, len(self._buckets))  # capacity may have changed
        self._buckets[index] = _Node(key, value, self._buckets[index])
        self._size += 1
        return None

    def get(self, key: K) -> V | None:
        """Return the value for key, or None when absent."""
        node = self._find_node(key)
        return None if node is None else node.value

    def remove(self, key: K) -> V | None:
        """Remove key if present. Return removed value, or None when absent."""
        index = self._index_for(key, len(self._buckets))
        previous: _Node[K, V] | None = None
        current = self._buckets[index]
        while current is not None:
            if current.key == key:
                if previous is None:
                    self._buckets[index] = current.next
                else:
                    previous.next = current.next
                self._size -= 1
                return current.value
            previous = current
            current = current.next
        return None

    def contains_key(self, key: K) -> bool:
        return self._find_node(key) is not None

    @property
    def size(self) -> int:
        return self._size

    @property
    def capacity(self) -> int:
        """Exposed for tests/demo to prove resize behavior."""
        return len(self._buckets)

    def _find_node(self, key: K) -> _Node[K, V] | None:
        current = self._buckets[self._index_for(key, len(self._buckets))]
        while current is not None:
            if current.key == key:
                return current
            current = current.next
        return None

    def _ensure_capacity_for(self, target_size: int) -> None:
        if target_size <= len(self._buckets) * self._load_factor:
            return
        self._resize(len(self._buckets) * 2)

    def _resize(self, new_capacity: int) -> None:
        old_buckets = self._buckets
        self._buckets = [None] * new_capacity

        # Rehash every node because a doubled power-of-two capacity changes bucket indexes.
        for head in old_buckets:
            current = head
            while current is not None:
                nxt = current.next
                index = self._index_for(current.key, new_capacity)
                current.next = self._buckets[index]
                self._buckets[index] = current
                current = nxt

    def _index_for(self, key: K, capacity: int) -> int:
        h = 0 if key is None else hash(key)
        h ^= h >> 16  # spread high bits into low bits before masking
        return h & (capacity - 1)  # valid because capacity is always a power of two

    @staticmethod
    def _next_power_of_two(value: int) -> int:
        capacity = 1
        while capacity < value:
            capacity <<= 1
        return capacity

    def __len__(self) -> int:  # pragma: no cover - trivial convenience
        return self._size

    def keys(self) -> Iterator[K]:  # pragma: no cover - helpful debugging convenience
        for head in self._buckets:
            current = head
            while current is not None:
                yield current.key
                current = current.next
