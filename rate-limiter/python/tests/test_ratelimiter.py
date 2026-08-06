"""End-to-end tests for the Rate Limiter MVP, including a concurrency race test."""

from __future__ import annotations

import threading
from datetime import datetime, timedelta, timezone

from ratelimiter.algorithms import FixedWindowLimiter, TokenBucketLimiter


class MutableClock:
    """Hand-advanced clock so refill/window tests are deterministic (no sleeps)."""

    def __init__(self, start: datetime) -> None:
        self._now = start

    def __call__(self) -> datetime:
        return self._now

    def advance(self, delta: timedelta) -> None:
        self._now += delta


def test_token_bucket_allows_capacity_then_denies_until_refill() -> None:
    clock = MutableClock(datetime(2024, 1, 1, tzinfo=timezone.utc))
    limiter = TokenBucketLimiter(capacity=3, refill_tokens_per_second=2.0, clock=clock)

    assert limiter.allow("client-a")
    assert limiter.allow("client-a")
    assert limiter.allow("client-a")
    assert not limiter.allow("client-a")

    clock.advance(timedelta(seconds=1))  # 2 tokens refilled at 2/sec
    assert limiter.allow("client-a")
    assert limiter.allow("client-a")
    assert not limiter.allow("client-a")


def test_fixed_window_allows_n_then_resets_after_window() -> None:
    clock = MutableClock(datetime(2024, 1, 1, tzinfo=timezone.utc))
    limiter = FixedWindowLimiter(max_requests_per_window=2, window_size=timedelta(seconds=10), clock=clock)

    assert limiter.allow("client-a")
    assert limiter.allow("client-a")
    assert not limiter.allow("client-a")

    clock.advance(timedelta(seconds=10))
    assert limiter.allow("client-a")
    assert limiter.allow("client-a")
    assert not limiter.allow("client-a")


def test_per_client_isolation() -> None:
    clock = MutableClock(datetime(2024, 1, 1, tzinfo=timezone.utc))
    limiter = TokenBucketLimiter(capacity=1, refill_tokens_per_second=1.0, clock=clock)

    assert limiter.allow("client-a")
    assert not limiter.allow("client-a")
    assert limiter.allow("client-b")


def test_concurrent_fixed_window_allows_exactly_capacity() -> None:
    limit = 5
    threads = 50
    clock = MutableClock(datetime(2024, 1, 1, tzinfo=timezone.utc))
    limiter = FixedWindowLimiter(max_requests_per_window=limit, window_size=timedelta(minutes=1), clock=clock)

    start = threading.Event()
    successes = 0
    successes_lock = threading.Lock()

    def worker() -> None:
        nonlocal successes
        start.wait()  # release all threads together for maximum contention
        if limiter.allow("client-a"):
            with successes_lock:
                successes += 1

    workers = [threading.Thread(target=worker) for _ in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert successes == limit, "exactly the configured limit should pass"
