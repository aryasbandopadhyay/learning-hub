"""Runnable demo: show allow/deny decisions for both algorithms.

Run:  python -m ratelimiter.main   (from the python/ directory)
"""

from __future__ import annotations

import time
from datetime import timedelta

from .algorithms import FixedWindowLimiter, TokenBucketLimiter


def _decision(allowed: bool) -> str:
    return "ALLOW" if allowed else "DENY"


def main() -> None:
    token_bucket = TokenBucketLimiter(capacity=3, refill_tokens_per_second=1.0)
    print("TokenBucket capacity=3 refill=1/sec")
    for i in range(1, 5):
        print(f"client-a request {i} -> {_decision(token_bucket.allow('client-a'))}")
    time.sleep(1.1)  # demo only; tests use MutableClock, never sleep
    print(f"client-a after refill -> {_decision(token_bucket.allow('client-a'))}")

    fixed_window = FixedWindowLimiter(max_requests_per_window=2, window_size=timedelta(seconds=60))
    print("FixedWindow max=2 window=60s")
    print(f"client-b request 1 -> {_decision(fixed_window.allow('client-b'))}")
    print(f"client-b request 2 -> {_decision(fixed_window.allow('client-b'))}")
    print(f"client-b request 3 -> {_decision(fixed_window.allow('client-b'))}")


if __name__ == "__main__":
    main()
