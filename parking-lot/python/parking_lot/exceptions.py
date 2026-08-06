"""Custom exceptions for the parking domain."""


class NoAvailableSpotError(Exception):
    """Raised when no compatible free spot exists for a vehicle."""


class InvalidTicketError(Exception):
    """Raised when a ticket id is unknown or has already been used at exit."""
