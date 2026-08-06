"""Eviction Strategy implementations for the in-memory cache MVP."""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections import OrderedDict
from dataclasses import dataclass
from typing import Generic, Hashable, TypeVar

K = TypeVar("K", bound=Hashable)


class EvictionPolicy(ABC, Generic[K]):
    """Strategy interface for access bookkeeping and victim selection.

    Cache owns the key->value dict. Policy objects own only ordering/frequency metadata. Adding FIFO
    later would mean writing another implementation of this interface, not changing Cache.
    """

    @abstractmethod
    def key_accessed(self, key: K) -> None:
        ...

    @abstractmethod
    def key_inserted(self, key: K) -> None:
        ...

    @abstractmethod
    def evict_key(self) -> K | None:
        ...


@dataclass
class _Node(Generic[K]):
    """Tiny metadata node; README UML calls this out as the per-key policy record."""

    key: K
    frequency: int = 1


class LruEvictionPolicy(EvictionPolicy[K]):
    """O(1) LRU using OrderedDict.

    OrderedDict is Python's production-ready version of the same interview data structure: a hash
    map from key to a doubly linked list node. ``move_to_end`` marks a key most-recent, and popping
    from the front evicts the least-recent key.
    """

    def __init__(self) -> None:
        self._order: OrderedDict[K, None] = OrderedDict()

    def key_accessed(self, key: K) -> None:
        if key in self._order:
            self._order.move_to_end(key)

    def key_inserted(self, key: K) -> None:
        self._order[key] = None
        self._order.move_to_end(key)

    def evict_key(self) -> K | None:
        if not self._order:
            return None
        key, _ = self._order.popitem(last=False)
        return key


class LfuEvictionPolicy(EvictionPolicy[K]):
    """O(1) LFU with frequency buckets and LRU tie-breaks.

    ``_nodes`` maps key -> frequency. ``_freq_to_keys`` maps frequency -> OrderedDict of keys in
    recency order inside that frequency. ``_min_freq`` points to the lowest non-empty bucket, so the
    victim is the first key in that bucket: LFU first, LRU as the tie-breaker.
    """

    def __init__(self) -> None:
        self._nodes: dict[K, _Node[K]] = {}
        self._freq_to_keys: dict[int, OrderedDict[K, None]] = {}
        self._min_freq = 0

    def key_accessed(self, key: K) -> None:
        node = self._nodes.get(key)
        if node is None:
            return
        old_freq = node.frequency
        bucket = self._freq_to_keys[old_freq]
        bucket.pop(key, None)
        if not bucket:
            self._freq_to_keys.pop(old_freq, None)
            if self._min_freq == old_freq:
                self._min_freq += 1
        node.frequency += 1
        self._freq_to_keys.setdefault(node.frequency, OrderedDict())[key] = None

    def key_inserted(self, key: K) -> None:
        self._nodes[key] = _Node(key)
        self._freq_to_keys.setdefault(1, OrderedDict())[key] = None
        self._min_freq = 1

    def evict_key(self) -> K | None:
        bucket = self._freq_to_keys.get(self._min_freq)
        if not bucket:
            return None
        victim, _ = bucket.popitem(last=False)
        if not bucket:
            self._freq_to_keys.pop(self._min_freq, None)
        self._nodes.pop(victim, None)
        return victim
