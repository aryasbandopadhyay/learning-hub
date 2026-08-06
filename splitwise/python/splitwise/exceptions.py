"""Domain exceptions for invalid Splitwise operations."""


class InvalidSplitError(ValueError):
    """Raised when split inputs are incomplete, negative, or do not add up."""
