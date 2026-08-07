"""Trie primitives: nodes, insertion, and prefix walking."""

from __future__ import annotations

from dataclasses import dataclass, field


@dataclass
class TrieNode:
    """One node in the Trie.

    Each edge is a character. A node becomes terminal when the path from the root forms a stored
    term/sentence. Terminal nodes keep the full term and its accumulated weight so DFS collection
    does not need to rebuild strings.
    """

    children: dict[str, "TrieNode"] = field(default_factory=dict)
    terminal: bool = False
    term: str | None = None
    weight: int = 0

    def child(self, ch: str) -> "TrieNode":
        """Return/create a child during insertion."""
        if ch not in self.children:
            self.children[ch] = TrieNode()
        return self.children[ch]

    def get_child(self, ch: str) -> "TrieNode | None":
        """Return an existing child during prefix walking, or None on a broken prefix."""
        return self.children.get(ch)

    def add_weight(self, term: str, delta: int) -> None:
        """Mark this node terminal and increment the historical frequency."""
        self.terminal = True
        self.term = term
        self.weight += delta


class Trie:
    """Small wrapper around the root node to make insert/walk intent explicit."""

    def __init__(self) -> None:
        self.root = TrieNode()

    def insert(self, term: str, weight: int) -> None:
        """Insert a term by creating one edge per character, then updating the terminal node."""
        node = self.root
        for ch in term:
            node = node.child(ch)
        node.add_weight(term, weight)

    def walk(self, prefix: str) -> TrieNode | None:
        """Walk from root to the node representing prefix; return None on the first miss."""
        node = self.root
        for ch in prefix:
            next_node = node.get_child(ch)
            if next_node is None:
                return None
            node = next_node
        return node
