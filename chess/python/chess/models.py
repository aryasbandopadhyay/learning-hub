"""Small shared model objects: Color and Cell."""

from __future__ import annotations

from dataclasses import dataclass
from enum import Enum


class Color(Enum):
    """The two chess sides. WHITE moves toward lower row numbers; BLACK moves downward."""

    WHITE = "WHITE"
    BLACK = "BLACK"

    @property
    def opposite(self) -> "Color":
        return Color.BLACK if self is Color.WHITE else Color.WHITE


@dataclass(frozen=True)
class Cell:
    """Immutable board coordinate. Rows/cols are 0-based; algebraic input is also supported."""

    row: int
    col: int

    def __post_init__(self) -> None:
        if not (0 <= self.row < 8 and 0 <= self.col < 8):
            raise ValueError(f"Cell outside board: {self.row},{self.col}")

    @classmethod
    def from_algebraic(cls, value: str) -> "Cell":
        if len(value) != 2:
            raise ValueError("Use algebraic notation like e2")
        file = value[0].lower()
        rank = value[1]
        if file < "a" or file > "h" or rank < "1" or rank > "8":
            raise ValueError(f"Cell outside board: {value}")
        return cls(ord("8") - ord(rank), ord(file) - ord("a"))

    def to_algebraic(self) -> str:
        return f"{chr(ord('a') + self.col)}{chr(ord('8') - self.row)}"
