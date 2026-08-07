"""Thread-safe bounded connection pool."""

from __future__ import annotations

import threading
import time
from collections import deque
from collections.abc import Callable

from .connection import Connection


class PoolTimeoutError(RuntimeError):
    """Raised when borrow waits for the timeout but no connection is released."""


class InvalidResourceError(RuntimeError):
    """Raised for foreign resources and double releases."""


ConnectionFactory = Callable[[str], Connection]
ValidationStrategy = Callable[[Connection], bool]


def default_factory(connection_id: str) -> Connection:
    """Factory abstraction: callers can swap creation without changing the pool."""

    return Connection(connection_id)


def default_validation(connection: Connection) -> bool:
    """Validation-on-borrow strategy; fake connections are valid while open."""

    return connection.is_valid()


class ConnectionPool:
    """Bounded, thread-safe generic resource pool.

    The MVP eagerly creates ``max_size`` connections, so allocation is capped by construction. A
    ``threading.Condition`` owns both the free deque and the borrowed set: ``borrow`` waits while the
    deque is empty, and ``release`` appends then notifies exactly one waiter.
    """

    def __init__(
        self,
        max_size: int,
        factory: ConnectionFactory = default_factory,
        validation_strategy: ValidationStrategy = default_validation,
    ) -> None:
        if max_size <= 0:
            raise ValueError("max_size must be positive")
        self._max_size = max_size
        self._validation = validation_strategy
        self._available: deque[Connection] = deque()
        self._all_ids: set[int] = set()
        self._borrowed_ids: set[int] = set()
        self._lock = threading.Lock()
        self._not_empty = threading.Condition(self._lock)

        for i in range(1, max_size + 1):
            connection = factory(f"conn-{i}")
            self._available.append(connection)
            self._all_ids.add(id(connection))

    def borrow(self, timeout: float) -> Connection:
        """Borrow a connection, blocking up to ``timeout`` seconds.

        The condition wait handles back-pressure: when all resources are checked out, callers sleep
        instead of spinning. Popping from ``_available`` and marking ``_borrowed_ids`` happen under
        the same lock, so a connection cannot be handed to two callers at once.
        """

        deadline = time.monotonic() + timeout
        with self._not_empty:
            while not self._available:
                remaining = deadline - time.monotonic()
                if remaining <= 0:
                    raise PoolTimeoutError("Timed out waiting for a connection")
                self._not_empty.wait(remaining)
            connection = self._available.popleft()
            self._borrowed_ids.add(id(connection))

        if not self._validation(connection):
            self.release(connection)
            raise InvalidResourceError(f"Borrowed connection is not valid: {connection.id}")
        return connection

    def release(self, connection: Connection) -> None:
        """Return a borrowed connection to the pool.

        Identity checks reject a connection created outside this pool and reject a second release of
        a resource that is already back in ``_available``.
        """

        with self._not_empty:
            identity = id(connection)
            if identity not in self._all_ids:
                raise InvalidResourceError("Connection does not belong to this pool")
            if identity not in self._borrowed_ids:
                raise InvalidResourceError("Connection was not borrowed or was already released")
            self._borrowed_ids.remove(identity)
            self._available.append(connection)
            self._not_empty.notify()

    @property
    def size(self) -> int:
        return self._max_size

    @property
    def available(self) -> int:
        with self._lock:
            return len(self._available)

    @property
    def borrowed(self) -> int:
        with self._lock:
            return len(self._borrowed_ids)
