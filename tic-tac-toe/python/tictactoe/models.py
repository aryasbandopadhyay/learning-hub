"""Domain models: marks, status, player, cell, and board."""

from __future__ import annotations

from dataclasses import dataclass
from enum import Enum

from .exceptions import InvalidMoveError


class Mark(Enum):
    """A cell mark. EMPTY avoids None checks throughout the board."""

    EMPTY = "EMPTY"
    X = "X"
    O = "O"


class GameStatus(Enum):
    """Finite states of one game; only IN_PROGRESS accepts moves."""

    IN_PROGRESS = "IN_PROGRESS"
    X_WON = "X_WON"
    O_WON = "O_WON"
    DRAW = "DRAW"


@dataclass(frozen=True)
class Player:
    """Immutable player identity: a display name plus the mark this player owns."""

    name: str
    mark: Mark

    def __post_init__(self) -> None:
        if not self.name:
            raise ValueError("Player name must be non-empty")
        if self.mark is Mark.EMPTY:
            raise ValueError("Player mark must be X or O")


@dataclass
class Cell:
    """A tiny mutable value object owned only by Board."""

    mark: Mark = Mark.EMPTY

    @property
    def is_empty(self) -> bool:
        return self.mark is Mark.EMPTY


class Board:
    """Owns the NxN grid and board-level rules: bounds, occupancy, writes, winner scans."""

    DEFAULT_SIZE = 3

    def __init__(self, size: int = DEFAULT_SIZE) -> None:
        if size < 3:
            raise ValueError("Board size must be at least 3")
        self.size = size
        self._cells = [[Cell() for _ in range(size)] for _ in range(size)]
        self._filled_cells = 0

    def mark_at(self, row: int, col: int) -> Mark:
        self._validate_bounds(row, col)
        return self._cells[row][col].mark

    def place_mark(self, row: int, col: int, mark: Mark) -> None:
        self._validate_bounds(row, col)
        cell = self._cells[row][col]
        if not cell.is_empty:
            raise InvalidMoveError(f"Cell ({row}, {col}) is already occupied")
        cell.mark = mark
        self._filled_cells += 1

    def is_full(self) -> bool:
        return self._filled_cells == self.size * self.size

    def has_winning_line(self, mark: Mark) -> bool:
        return self._has_winning_row(mark) or self._has_winning_column(mark) or self._has_winning_diagonal(mark)

    def _has_winning_row(self, mark: Mark) -> bool:
        return any(all(cell.mark is mark for cell in row) for row in self._cells)

    def _has_winning_column(self, mark: Mark) -> bool:
        return any(all(self._cells[row][col].mark is mark for row in range(self.size)) for col in range(self.size))

    def _has_winning_diagonal(self, mark: Mark) -> bool:
        main = all(self._cells[i][i].mark is mark for i in range(self.size))
        anti = all(self._cells[i][self.size - 1 - i].mark is mark for i in range(self.size))
        return main or anti

    def _validate_bounds(self, row: int, col: int) -> None:
        if row < 0 or row >= self.size or col < 0 or col >= self.size:
            raise InvalidMoveError(f"Cell ({row}, {col}) is outside the {self.size}x{self.size} board")

    def __str__(self) -> str:  # pragma: no cover - trivial formatting for the demo
        return "\n".join(" ".join("." if cell.mark is Mark.EMPTY else cell.mark.value for cell in row) for row in self._cells)
