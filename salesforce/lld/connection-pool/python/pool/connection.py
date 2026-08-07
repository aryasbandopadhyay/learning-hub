"""Fake in-memory connection model used by the pool."""

from __future__ import annotations

import threading


class Connection:
    """A reusable resource with only id + lifecycle methods.

    The Salesforce-style LLD prompt is about pooling and concurrency, not real I/O, so this object
    never opens a DB/network socket. ``close`` just flips an in-memory flag protected by a lock.
    """

    def __init__(self, connection_id: str) -> None:
        self.id = connection_id
        self._open = True
        self._lock = threading.Lock()

    def is_open(self) -> bool:
        with self._lock:
            return self._open

    def is_valid(self) -> bool:
        with self._lock:
            return self._open

    def close(self) -> None:
        with self._lock:
            self._open = False

    def __repr__(self) -> str:  # pragma: no cover - trivial
        return f"Connection({self.id})"
