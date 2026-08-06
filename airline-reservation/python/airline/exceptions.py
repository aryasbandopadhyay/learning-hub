"""Custom exceptions for the airline reservation domain."""


class FlightNotFoundError(Exception):
    """Raised when a requested flight number is unknown."""


class SeatAlreadyBookedError(Exception):
    """Raised when a specific seat is already BOOKED."""


class NoSeatAvailableError(Exception):
    """Raised when no seat exists or no seat is free in the requested cabin."""


class BookingNotFoundError(Exception):
    """Raised when a PNR is unknown or has already been cancelled."""
