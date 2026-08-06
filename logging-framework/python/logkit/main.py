"""Runnable demo: configure LogManager, log a few messages, and print captured count.

Run:  python -m logkit.main   (from the python/ directory)
"""

from __future__ import annotations

from datetime import datetime, timezone

from .appenders import ConsoleAppender, InMemoryAppender
from .level import LogLevel
from .manager import LogManager


def fixed_clock() -> datetime:
    return datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc)


def main() -> None:
    manager = LogManager.instance()
    audit = InMemoryAppender()
    manager.configure_root(LogLevel.INFO, [ConsoleAppender(), audit], fixed_clock)

    logger = manager.get_logger("checkout")
    logger.debug("debug details are below INFO and are dropped")
    logger.info("order created")
    logger.warn("payment retry scheduled")
    logger.error("payment failed")

    print("In-memory records:", len(audit))


if __name__ == "__main__":
    main()
