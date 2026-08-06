"""Rate Limiter — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one so the two can be compared:

    limiter.py     -> RateLimiter abstraction
    algorithms.py  -> TokenBucketLimiter and FixedWindowLimiter Strategy implementations
    exceptions.py  -> InvalidRateLimitConfigurationError
    main.py        -> runnable demo

Concurrency: each client has independent state protected by that client's Lock, so refill/check/
consume (or reset/check/increment) is atomic per client without blocking unrelated clients.
"""

from .algorithms import FixedWindowLimiter, TokenBucketLimiter
from .limiter import RateLimiter

__all__ = ["RateLimiter", "TokenBucketLimiter", "FixedWindowLimiter"]
