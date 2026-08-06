"""Domain-specific exceptions for clearer tests and caller handling."""


class CarUnavailableError(RuntimeError):
    """Raised when a requested car is already booked for overlapping dates."""


class InvalidDateRangeError(ValueError):
    """Raised when pickup is not strictly before return."""


class InvalidReservationStateError(RuntimeError):
    """Raised when a reservation lifecycle transition is invalid."""


class ReservationNotFoundError(KeyError):
    """Raised when a reservation id is unknown."""
