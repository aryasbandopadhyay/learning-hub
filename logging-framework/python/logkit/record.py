"""Immutable value object created once for every accepted log event."""

from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime

from .level import LogLevel


@dataclass(frozen=True)
class LogRecord:
    level: LogLevel
    message: str
    logger_name: str
    timestamp: datetime
    thread_name: str
