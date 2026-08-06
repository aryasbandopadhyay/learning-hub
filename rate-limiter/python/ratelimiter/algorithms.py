"""Strategy implementations: Token Bucket and Fixed Window."""

from __future__ import annotations

import threading
from dataclasses import dataclass
from datetime import datetime, timezone, timedelta
from typing import Callable

from .exceptions import InvalidRateLimitConfigurationError
from .limiter import RateLimiter

Clock = Callable[[], datetime]


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


@dataclass
class _BucketState:
    tokens: float
    last_refill_time: datetime
    lock: threading.Lock


class TokenBucketLimiter(RateLimiter):
    """Token Bucket rate limiter.

    Each client owns a bucket with up to ``capacity`` tokens. ``allow`` refills tokens from elapsed
    time, then consumes one token if available. The design allows bursts up to capacity while keeping
    the long-term average at ``refill_tokens_per_second``.

    Concurrency: the dict lock only creates/looks up a per-client state object. The state lock makes
    refill + check + consume one atomic critical section for that client.
    """

    def __init__(
        self,
        capacity: int,
        refill_tokens_per_second: float,
        clock: Clock = _utc_now,
    ) -> None:
        if capacity <= 0:
            raise InvalidRateLimitConfigurationError("capacity must be positive")
        if refill_tokens_per_second <= 0:
            raise InvalidRateLimitConfigurationError("refill rate must be positive")
        self._capacity = capacity
        self._refill_rate = refill_tokens_per_second
        self._clock = clock
        self._buckets: dict[str, _BucketState] = {}
        self._buckets_lock = threading.Lock()

    def allow(self, client_id: str) -> bool:
        bucket = self._bucket_for(client_id)
        with bucket.lock:
            self._refill(bucket)
            if bucket.tokens >= 1.0:
                bucket.tokens -= 1.0
                return True
            return False

    def _bucket_for(self, client_id: str) -> _BucketState:
        with self._buckets_lock:
            bucket = self._buckets.get(client_id)
            if bucket is None:
                bucket = _BucketState(self._capacity, self._clock(), threading.Lock())
                self._buckets[client_id] = bucket
            return bucket

    def _refill(self, bucket: _BucketState) -> None:
        now = self._clock()
        elapsed = max(0.0, (now - bucket.last_refill_time).total_seconds())
        if elapsed == 0.0:
            return
        bucket.tokens = min(self._capacity, bucket.tokens + elapsed * self._refill_rate)
        bucket.last_refill_time = now


@dataclass
class _WindowState:
    window_number: int
    count: int
    lock: threading.Lock


class FixedWindowLimiter(RateLimiter):
    """Fixed Window rate limiter.

    Time is divided into equal windows. Each client gets ``max_requests_per_window`` requests in the
    current window, and the counter resets when the injected clock moves into the next window.

    It is simple and memory-cheap, but it may allow boundary bursts: N calls at the end of one window
    plus N calls at the beginning of the next.
    """

    def __init__(
        self,
        max_requests_per_window: int,
        window_size: timedelta,
        clock: Clock = _utc_now,
    ) -> None:
        if max_requests_per_window <= 0:
            raise InvalidRateLimitConfigurationError("max requests must be positive")
        if window_size <= timedelta(0):
            raise InvalidRateLimitConfigurationError("window size must be positive")
        self._max_requests = max_requests_per_window
        self._window_size = window_size
        self._clock = clock
        self._windows: dict[str, _WindowState] = {}
        self._windows_lock = threading.Lock()

    def allow(self, client_id: str) -> bool:
        window_number = self._current_window_number()
        state = self._window_for(client_id, window_number)
        with state.lock:
            if state.window_number != window_number:
                state.window_number = window_number
                state.count = 0
            if state.count < self._max_requests:
                state.count += 1
                return True
            return False

    def _window_for(self, client_id: str, window_number: int) -> _WindowState:
        with self._windows_lock:
            state = self._windows.get(client_id)
            if state is None:
                state = _WindowState(window_number, 0, threading.Lock())
                self._windows[client_id] = state
            return state

    def _current_window_number(self) -> int:
        epoch_seconds = self._clock().timestamp()
        return int(epoch_seconds // self._window_size.total_seconds())
