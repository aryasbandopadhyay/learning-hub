"""Formatter strategies: appenders depend on this abstraction, not on one string layout."""

from __future__ import annotations

from abc import ABC, abstractmethod

from .record import LogRecord


class Formatter(ABC):
    @abstractmethod
    def format(self, record: LogRecord) -> str:
        """Render one record as text."""


class SimpleFormatter(Formatter):
    """Human-readable default: [timestamp] LEVEL logger [thread] - message."""

    def format(self, record: LogRecord) -> str:
        timestamp = record.timestamp.isoformat().replace("+00:00", "Z")
        return (
            f"[{timestamp}] {record.level.name} {record.logger_name} "
            f"[{record.thread_name}] - {record.message}"
        )
