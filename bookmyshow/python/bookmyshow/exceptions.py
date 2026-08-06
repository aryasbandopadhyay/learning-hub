"""Domain exceptions for the BookMyShow MVP."""


class SeatUnavailableError(Exception):
    """Raised when at least one requested seat is not available, so the whole hold fails."""


class HoldExpiredError(Exception):
    """Raised when a client tries to confirm after the hold expiry."""


class NotFoundError(Exception):
    """Raised for unknown shows, seats, or holds."""

