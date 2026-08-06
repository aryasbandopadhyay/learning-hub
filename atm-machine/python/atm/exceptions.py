"""Domain exceptions for invalid ATM flows and money/cash failures."""


class AtmError(Exception):
    """Base error for failures callers can show to an ATM user."""


class InvalidOperationError(AtmError):
    """Operation is not legal in the current State object."""


class AuthenticationError(AtmError):
    """Wrong PIN attempt or forced eject after too many failures."""


class InsufficientFundsError(AtmError):
    """Account does not have enough balance for the requested withdrawal."""


class CashDispenseError(AtmError):
    """ATM cannot produce an exact denomination breakdown."""
