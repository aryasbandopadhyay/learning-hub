"""The public RateLimiter abstraction."""

from __future__ import annotations

from abc import ABC, abstractmethod


class RateLimiter(ABC):
    """Strategy interface for per-client rate limiting.

    Callers only know about ``allow(client_id)``. Concrete strategies can use a token bucket, fixed
    window, sliding log, or future Redis-backed algorithm without changing callers.
    """

    @abstractmethod
    def allow(self, client_id: str) -> bool:
        """Return True when this request may pass; False when it should be throttled."""
