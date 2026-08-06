"""Custom exceptions for the Chess MVP."""


class InvalidMoveError(Exception):
    """Raised when a move violates turn, ownership, geometry, path, or capture rules."""
