"""Custom exceptions for the library domain."""


class NoAvailableCopyError(Exception):
    """Raised when all copies of a requested book are currently loaned out."""


class LoanLimitExceededError(Exception):
    """Raised when a member already has the maximum allowed active loans."""


class InvalidLoanError(Exception):
    """Raised when a loan id is unknown or has already been returned."""
