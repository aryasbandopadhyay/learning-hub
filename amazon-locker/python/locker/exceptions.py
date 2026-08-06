"""Custom exceptions for the locker domain."""


class NoAvailableLockerError(Exception):
    """Raised when no compatible free locker exists for a package."""


class InvalidPickupCodeError(Exception):
    """Raised when a pickup code is unknown or has already been used."""
