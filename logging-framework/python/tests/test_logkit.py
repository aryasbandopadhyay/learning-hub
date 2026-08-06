"""End-to-end tests for the Logging Framework MVP, including concurrent logging."""

from __future__ import annotations

import threading
from datetime import datetime, timezone

from logkit.appenders import InMemoryAppender
from logkit.formatters import SimpleFormatter
from logkit.level import LogLevel
from logkit.logger import Logger
from logkit.manager import LogManager


def fixed_clock() -> datetime:
    return datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc)


def make_logger(level: LogLevel, *appenders: InMemoryAppender) -> Logger:
    return Logger("orders", level, appenders, fixed_clock)


def test_level_filtering_drops_below_threshold():
    memory = InMemoryAppender()
    logger = make_logger(LogLevel.WARN, memory)

    logger.debug("hidden debug")
    logger.info("hidden info")
    logger.warn("visible warn")
    logger.error("visible error")

    assert len(memory) == 2
    assert memory.records[0].level is LogLevel.WARN
    assert memory.records[1].level is LogLevel.ERROR


def test_simple_formatter_includes_expected_fields():
    memory = InMemoryAppender(SimpleFormatter())
    logger = make_logger(LogLevel.DEBUG, memory)

    logger.info("created")

    line = memory.lines[0]
    assert line.startswith("[2024-01-01T10:00:00Z] INFO orders [")
    assert line.endswith("] - created")


def test_multiple_appenders_each_receive_the_record():
    first = InMemoryAppender()
    second = InMemoryAppender()
    logger = make_logger(LogLevel.INFO, first, second)

    logger.info("fan out")

    assert len(first) == 1
    assert len(second) == 1
    assert first.records[0].message == "fan out"
    assert second.records[0].message == "fan out"


def test_log_manager_returns_same_logger_for_same_name():
    manager = LogManager.instance()
    manager.reset_for_tests()

    a = manager.get_logger("billing")
    b = manager.get_logger("billing")

    assert a is b


def test_concurrent_logging_does_not_lose_records():
    threads = 20
    messages_per_thread = 100
    memory = InMemoryAppender()
    logger = make_logger(LogLevel.DEBUG, memory)
    start = threading.Event()

    def worker(worker_id: int) -> None:
        start.wait()
        for i in range(messages_per_thread):
            logger.info(f"worker-{worker_id} message-{i}")

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert len(memory) == threads * messages_per_thread
