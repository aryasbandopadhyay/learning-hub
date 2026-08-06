"""Turn-based game service: validates and applies moves."""

from __future__ import annotations

from dataclasses import dataclass

from .board import Board
from .exceptions import InvalidMoveError
from .models import Cell, Color
from .pieces import Piece


@dataclass(frozen=True)
class Move:
    """Immutable value object representing one requested move."""

    from_cell: Cell
    to_cell: Cell

    @classmethod
    def of(cls, from_square: str, to_square: str) -> "Move":
        return cls(Cell.from_algebraic(from_square), Cell.from_algebraic(to_square))


class Game:
    """Application service for turn-based play.

    Game enforces orchestration rules (whose turn, source has a piece, no own capture), then
    delegates geometry to the piece polymorphically. Chess is turn-based, so this MVP needs no
    concurrency control; a UI/API would serialize one move request at a time.
    """

    def __init__(self, board: Board | None = None, starting_turn: Color = Color.WHITE) -> None:
        self.board = board or Board()
        self.current_turn = starting_turn
        self.captured_pieces: list[Piece] = []

    def make_move(self, from_square: str | Move, to_square: str | None = None) -> None:
        move = from_square if isinstance(from_square, Move) else Move.of(from_square, to_square or "")
        piece = self.board.get_piece(move.from_cell)
        if piece is None:
            raise InvalidMoveError(f"No piece at {move.from_cell.to_algebraic()}")
        if piece.color is not self.current_turn:
            raise InvalidMoveError(f"It is {self.current_turn.value}'s turn, not {piece.color.value}")
        if self.board.has_own_piece(move.to_cell, self.current_turn):
            raise InvalidMoveError(f"Cannot capture your own piece at {move.to_cell.to_algebraic()}")
        if not piece.is_valid_move(self.board, move.from_cell, move.to_cell):
            raise InvalidMoveError(
                f"Illegal {piece.__class__.__name__} move: "
                f"{move.from_cell.to_algebraic()} -> {move.to_cell.to_algebraic()}"
            )
        captured = self.board.get_piece(move.to_cell)
        if captured is not None:
            self.captured_pieces.append(captured)
        self.board.move_piece(move.from_cell, move.to_cell)
        self.current_turn = self.current_turn.opposite

    def is_in_check(self, color: Color) -> bool:
        return self.board.is_in_check(color)
