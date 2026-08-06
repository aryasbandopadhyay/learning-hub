"""Custom exceptions for the Tic Tac Toe domain."""


class InvalidMoveError(Exception):
    """Raised when a move violates bounds, occupancy, turn order, or terminal-game rules."""
