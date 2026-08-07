"""Domain models: immutable Message and append-only Topic."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import Callable


@dataclass(frozen=True)
class Message:
    """Immutable event flowing through the broker.

    The broker owns ``id`` and ``offset``. Subscribers receive the same Message instance, but the
    frozen dataclass prevents one subscriber from mutating shared state seen by another subscriber.
    """

    topic_name: str
    offset: int
    payload: str
    published_at: datetime
    id: str = field(default_factory=lambda: str(uuid.uuid4()))


class Topic:
    """A named append-only stream of messages.

    ``append`` is the only place where offsets are assigned. The lock protects the short critical
    section that reads/increments ``_next_offset`` and appends to history; delivery happens outside
    this class so publishers do not hold this lock while subscriber callbacks run.
    """

    def __init__(self, name: str) -> None:
        if not name or not name.strip():
            raise ValueError("topic name must not be blank")
        self.name = name
        self._next_offset = 0
        self._messages: list[Message] = []
        self._lock = threading.Lock()

    def append(self, payload: str, clock: Callable[[], datetime]) -> Message:
        """Append one payload and return the immutable Message assigned by this topic."""
        with self._lock:
            message = Message(
                topic_name=self.name,
                offset=self._next_offset,
                payload=payload,
                published_at=clock(),
            )
            self._next_offset += 1
            self._messages.append(message)
            return message

    def snapshot(self) -> tuple[Message, ...]:
        """Return an immutable snapshot for tests/introspection."""
        with self._lock:
            return tuple(self._messages)


def utc_now() -> datetime:
    return datetime.now(timezone.utc)
