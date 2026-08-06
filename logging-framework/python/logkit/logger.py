"""Named Logger: threshold filtering, record creation, and fan-out to appenders."""

from __future__ import annotations

import threading
from datetime import datetime, timezone
from typing import Callable, Iterable

from .appenders import Appender
from .level import LogLevel
from .record import LogRecord

Clock = Callable[[], datetime]


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


class Logger:
    """A small, thread-safe logger.

    The logger keeps its appender list behind an RLock so configuration can change safely. Each
    accepted event is an immutable LogRecord; appender implementations then guard their own writes.
    """

    def __init__(
        self,
        name: str,
        minimum_level: LogLevel,
        appenders: Iterable[Appender],
        clock: Clock = utc_now,
    ) -> None:
        self.name = name
        self._minimum_level = minimum_level
        self._appenders = list(appenders)
        self._clock = clock
        self._lock = threading.RLock()

    def debug(self, message: str) -> None:
        self.log(LogLevel.DEBUG, message)

    def info(self, message: str) -> None:
        self.log(LogLevel.INFO, message)

    def warn(self, message: str) -> None:
        self.log(LogLevel.WARN, message)

    def error(self, message: str) -> None:
        self.log(LogLevel.ERROR, message)

    def log(self, level: LogLevel, message: str) -> None:
        with self._lock:
            if not level.is_at_least(self._minimum_level):
                return
            appenders = tuple(self._appenders)
        record = LogRecord(
            level=level,
            message=message,
            logger_name=self.name,
            timestamp=self._clock(),
            thread_name=threading.current_thread().name,
        )
        for appender in appenders:
            appender.append(record)

    def add_appender(self, appender: Appender) -> None:
        with self._lock:
            self._appenders.append(appender)

    def clear_appenders(self) -> None:
        with self._lock:
            self._appenders.clear()

    @property
    def minimum_level(self) -> LogLevel:
        with self._lock:
            return self._minimum_level

    @minimum_level.setter
    def minimum_level(self, level: LogLevel) -> None:
        with self._lock:
            self._minimum_level = level
