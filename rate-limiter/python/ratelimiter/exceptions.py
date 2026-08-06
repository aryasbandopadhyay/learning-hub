"""Domain-specific exceptions for limiter construction errors."""


class InvalidRateLimitConfigurationError(ValueError):
    """Raised when a limiter receives nonsensical limits such as zero capacity."""
