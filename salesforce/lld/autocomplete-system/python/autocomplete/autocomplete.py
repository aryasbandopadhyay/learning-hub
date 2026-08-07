"""Application service for Trie-backed autocomplete."""

from __future__ import annotations

import heapq
from dataclasses import dataclass

from .trie import Trie, TrieNode


@dataclass(frozen=True)
class RankedTerm:
    """Candidate used while ranking suggestions."""

    term: str
    weight: int


@dataclass(frozen=True)
class _HeapItem:
    """Heap wrapper whose natural order puts the worst suggestion first."""

    weight: int
    term: str

    def __lt__(self, other: "_HeapItem") -> bool:
        if self.weight != other.weight:
            return self.weight < other.weight
        return self.term > other.term


class AutocompleteSystem:
    """Trie-backed typeahead service.

    The MVP is intentionally single-threaded, matching the common LeetCode 642 object model where
    ``input`` owns mutable in-progress sentence state. A production service would guard Trie updates
    and reads with a lock, and keep input buffers per user/session.
    """

    DEFAULT_INTERACTIVE_LIMIT = 3

    def __init__(self, terms: list[str] | None = None, weights: list[int] | None = None) -> None:
        self._trie = Trie()
        self._current_input: list[str] = []

        terms = terms or []
        weights = weights or []
        if len(terms) != len(weights):
            raise ValueError("terms and weights must have the same size")
        for term, weight in zip(terms, weights):
            self.add_term(term, weight)

    def add_term(self, term: str, weight: int) -> None:
        """Insert a new term or increment the frequency of an existing one."""
        if not term:
            raise ValueError("term must not be empty")
        if weight <= 0:
            raise ValueError("weight must be positive")
        self._trie.insert(term, weight)

    def suggest(self, prefix: str, k: int) -> list[str]:
        """Return top-k terms sharing prefix, ranked by frequency desc then lexicographic asc."""
        if prefix is None:
            raise ValueError("prefix must not be None")
        if k <= 0:
            return []

        node = self._trie.walk(prefix)
        if node is None:
            return []

        # The root is the worst candidate, so when the heap grows beyond k, one heappop removes the
        # candidate that cannot belong in top-k.
        top_k: list[_HeapItem] = []
        self._collect(node, top_k, k)

        ranked = [RankedTerm(term=item.term, weight=item.weight) for item in top_k]
        ranked.sort(key=lambda candidate: (-candidate.weight, candidate.term))
        return [candidate.term for candidate in ranked]

    def input(self, ch: str) -> list[str]:
        """LC642-style streaming API: '#' commits the buffer; other chars return suggestions."""
        if len(ch) != 1:
            raise ValueError("input expects a single character")
        if ch == "#":
            if self._current_input:
                self.add_term("".join(self._current_input), 1)
                self._current_input.clear()
            return []
        self._current_input.append(ch)
        return self.suggest("".join(self._current_input), self.DEFAULT_INTERACTIVE_LIMIT)

    def _collect(self, node: TrieNode, top_k: list[_HeapItem], k: int) -> None:
        """DFS over the matching subtree, maintaining a bounded heap of best k terminal nodes."""
        if node.terminal:
            assert node.term is not None
            heapq.heappush(top_k, _HeapItem(node.weight, node.term))
            if len(top_k) > k:
                heapq.heappop(top_k)
        for child in node.children.values():
            self._collect(child, top_k, k)
