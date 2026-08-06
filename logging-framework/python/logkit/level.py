"""Severity levels. IntEnum makes DEBUG < INFO < WARN < ERROR directly comparable."""

from __future__ import annotations

from enum import IntEnum


class LogLevel(IntEnum):
    DEBUG = 10
    INFO = 20
    WARN = 30
    ERROR = 40

    def is_at_least(self, threshold: "LogLevel") -> bool:
        return self >= threshold
