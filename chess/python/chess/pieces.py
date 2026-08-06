"""Piece hierarchy: polymorphic movement validation is the centerpiece of this MVP."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

from .models import Cell, Color

if TYPE_CHECKING:  # pragma: no cover
    from .board import Board


class Piece(ABC):
    """Abstract base of the chess-piece hierarchy (inheritance + polymorphism).

    Every concrete piece overrides ``is_valid_move`` with its own geometry. ``Game`` does not use a
    big switch on piece type; it simply calls this method on the runtime object.
    """

    def __init__(self, color: Color) -> None:
        self.color = color

    @abstractmethod
    def is_valid_move(self, board: "Board", from_cell: Cell, to_cell: Cell) -> bool:
        """Piece-specific movement rule. Turn management belongs to Game, not Piece."""

    def _destination_is_not_own_piece(self, board: "Board", to_cell: Cell) -> bool:
        return not board.has_own_piece(to_cell, self.color)

    @staticmethod
    def _is_straight(from_cell: Cell, to_cell: Cell) -> bool:
        return from_cell.row == to_cell.row or from_cell.col == to_cell.col

    @staticmethod
    def _is_diagonal(from_cell: Cell, to_cell: Cell) -> bool:
        return abs(from_cell.row - to_cell.row) == abs(from_cell.col - to_cell.col)

    @property
    def symbol(self) -> str:  # pragma: no cover - display helper
        return self.__class__.__name__[0]


class Rook(Piece):
    """Rook: any number of squares horizontally or vertically, but never through pieces."""

    def is_valid_move(self, board: "Board", from_cell: Cell, to_cell: Cell) -> bool:
        return (
            from_cell != to_cell
            and self._is_straight(from_cell, to_cell)
            and board.is_path_clear(from_cell, to_cell)
            and self._destination_is_not_own_piece(board, to_cell)
        )


class Bishop(Piece):
    """Bishop: any number of diagonal squares, with a clear path required."""

    def is_valid_move(self, board: "Board", from_cell: Cell, to_cell: Cell) -> bool:
        return (
            from_cell != to_cell
            and self._is_diagonal(from_cell, to_cell)
            and board.is_path_clear(from_cell, to_cell)
            and self._destination_is_not_own_piece(board, to_cell)
        )


class Queen(Piece):
    """Queen combines rook and bishop geometry: straight or diagonal, with a clear path."""

    def is_valid_move(self, board: "Board", from_cell: Cell, to_cell: Cell) -> bool:
        return (
            from_cell != to_cell
            and (self._is_straight(from_cell, to_cell) or self._is_diagonal(from_cell, to_cell))
            and board.is_path_clear(from_cell, to_cell)
            and self._destination_is_not_own_piece(board, to_cell)
        )


class Knight(Piece):
    """Knight: L-shape move. It intentionally ignores path blockers."""

    def is_valid_move(self, board: "Board", from_cell: Cell, to_cell: Cell) -> bool:
        dr = abs(from_cell.row - to_cell.row)
        dc = abs(from_cell.col - to_cell.col)
        return ((dr == 2 and dc == 1) or (dr == 1 and dc == 2)) and self._destination_is_not_own_piece(
            board, to_cell
        )


class King(Piece):
    """King: one square in any direction. Castling is intentionally outside this MVP."""

    def is_valid_move(self, board: "Board", from_cell: Cell, to_cell: Cell) -> bool:
        dr = abs(from_cell.row - to_cell.row)
        dc = abs(from_cell.col - to_cell.col)
        return from_cell != to_cell and dr <= 1 and dc <= 1 and self._destination_is_not_own_piece(board, to_cell)


class Pawn(Piece):
    """Pawn: forward movement, initial two-square advance, and diagonal capture."""

    def is_valid_move(self, board: "Board", from_cell: Cell, to_cell: Cell) -> bool:
        if from_cell == to_cell or not self._destination_is_not_own_piece(board, to_cell):
            return False
        direction = -1 if self.color is Color.WHITE else 1
        start_row = 6 if self.color is Color.WHITE else 1
        row_delta = to_cell.row - from_cell.row
        col_delta = abs(to_cell.col - from_cell.col)

        if col_delta == 0 and row_delta == direction and board.is_empty(to_cell):
            return True
        if col_delta == 0 and from_cell.row == start_row and row_delta == 2 * direction:
            between = Cell(from_cell.row + direction, from_cell.col)
            return board.is_empty(between) and board.is_empty(to_cell)
        return col_delta == 1 and row_delta == direction and board.has_enemy_piece(to_cell, self.color)
