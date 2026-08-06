"""Singleton LogManager: root configuration plus named-logger factory/cache."""

from __future__ import annotations

import threading
from typing import Iterable

from .appenders import Appender, ConsoleAppender
from .level import LogLevel
from .logger import Clock, Logger, utc_now


class LogManager:
    """Process-wide registry.

    ``instance`` implements the Singleton pattern: every caller receives the same manager. ``get_logger``
    is also a Factory Method that hides Logger construction and caches by name.
    """

    _instance: "LogManager | None" = None
    _instance_lock = threading.Lock()

    def __init__(self) -> None:
        self._loggers: dict[str, Logger] = {}
        self._default_level = LogLevel.INFO
        self._root_appenders: list[Appender] = [ConsoleAppender()]
        self._clock: Clock = utc_now
        self._lock = threading.RLock()

    @classmethod
    def instance(cls) -> "LogManager":
        with cls._instance_lock:
            if cls._instance is None:
                cls._instance = cls()
            return cls._instance

    def get_logger(self, name: str) -> Logger:
        with self._lock:
            if name not in self._loggers:
                self._loggers[name] = Logger(
                    name,
                    self._default_level,
                    tuple(self._root_appenders),
                    self._clock,
                )
            return self._loggers[name]

    def configure_root(
        self,
        level: LogLevel,
        appenders: Iterable[Appender],
        clock: Clock = utc_now,
    ) -> None:
        with self._lock:
            self._default_level = level
            self._root_appenders = list(appenders)
            self._clock = clock

    def reset_for_tests(self) -> None:
        with self._lock:
            self._loggers.clear()
            self._default_level = LogLevel.INFO
            self._root_appenders = [ConsoleAppender()]
            self._clock = utc_now
