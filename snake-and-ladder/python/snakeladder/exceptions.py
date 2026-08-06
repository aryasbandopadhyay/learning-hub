"""Custom exceptions for invalid board config and invalid game progression."""


class InvalidBoardError(ValueError):
    """Raised when snakes/ladders make the board ambiguous or impossible."""


class GameAlreadyOverError(RuntimeError):
    """Raised if play continues after a winner has already been declared."""
