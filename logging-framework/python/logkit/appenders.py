"""Appender sinks. Each appender guards its own writes for thread safety."""

from __future__ import annotations

import threading
from pathlib import Path
from typing import Protocol

from .formatters import Formatter, SimpleFormatter
from .record import LogRecord


class Appender(Protocol):
    """Observer-like sink called by Logger for each accepted record."""

    def append(self, record: LogRecord) -> None:
        ...


class ConsoleAppender:
    """Console sink. The lock prevents lines from concurrent threads interleaving."""

    def __init__(self, formatter: Formatter | None = None) -> None:
        self._formatter = formatter or SimpleFormatter()
        self._lock = threading.Lock()

    def append(self, record: LogRecord) -> None:
        with self._lock:
            print(self._formatter.format(record))


class InMemoryAppender:
    """Test sink. Records and formatted lines are snapshotted under the same lock."""

    def __init__(self, formatter: Formatter | None = None) -> None:
        self._formatter = formatter or SimpleFormatter()
        self._records: list[LogRecord] = []
        self._lines: list[str] = []
        self._lock = threading.Lock()

    def append(self, record: LogRecord) -> None:
        with self._lock:
            self._records.append(record)
            self._lines.append(self._formatter.format(record))

    @property
    def records(self) -> tuple[LogRecord, ...]:
        with self._lock:
            return tuple(self._records)

    @property
    def lines(self) -> tuple[str, ...]:
        with self._lock:
            return tuple(self._lines)

    def __len__(self) -> int:
        with self._lock:
            return len(self._records)


class FileAppender:
    """File sink. The lock serializes append mode writes from all logging threads."""

    def __init__(self, path: str | Path, formatter: Formatter | None = None) -> None:
        self._path = Path(path)
        self._formatter = formatter or SimpleFormatter()
        self._lock = threading.Lock()

    def append(self, record: LogRecord) -> None:
        line = self._formatter.format(record)
        with self._lock:
            self._path.parent.mkdir(parents=True, exist_ok=True)
            with self._path.open("a", encoding="utf-8") as handle:
                handle.write(line + "\n")
