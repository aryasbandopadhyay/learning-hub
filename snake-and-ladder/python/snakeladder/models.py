"""Domain models: Board/Cell/Jump plus Player token state."""

from __future__ import annotations

from dataclasses import dataclass
from enum import Enum
from types import MappingProxyType
from typing import Mapping, Sequence

from .exceptions import InvalidBoardError


class JumpType(Enum):
    """A jump is either a helpful ladder or a harmful snake."""

    SNAKE = "SNAKE"
    LADDER = "LADDER"


@dataclass(frozen=True)
class Jump:
    """Directed edge: landing on ``from_cell`` instantly moves the player to ``to_cell``."""

    from_cell: int
    to_cell: int
    type: JumpType

    @classmethod
    def snake(cls, head: int, tail: int) -> "Jump":
        if head <= tail:
            raise InvalidBoardError("Snake head must be greater than tail")
        return cls(head, tail, JumpType.SNAKE)

    @classmethod
    def ladder(cls, bottom: int, top: int) -> "Jump":
        if top <= bottom:
            raise InvalidBoardError("Ladder top must be greater than bottom")
        return cls(bottom, top, JumpType.LADDER)

    @property
    def is_ladder(self) -> bool:
        return self.type is JumpType.LADDER


@dataclass(frozen=True)
class Cell:
    """One numbered square. At most one outgoing snake/ladder can start here."""

    number: int
    jump: Jump | None = None


class Board:
    """Immutable board definition, validated before any turns are played.

    Cells are numbered 1..size. Cell 1 is the starting square and ``size`` is the winning square.
    Duplicate jump starts are rejected because landing on that cell would otherwise be ambiguous.
    """

    DEFAULT_SIZE = 100

    def __init__(self, size: int = DEFAULT_SIZE, jumps: Sequence[Jump] = ()) -> None:
        if size < 2:
            raise InvalidBoardError("Board must have at least two cells")
        self.size = size
        self._jumps_by_start = MappingProxyType(self._validate_and_index_jumps(size, jumps))
        self.cells = tuple(Cell(i, self._jumps_by_start.get(i)) for i in range(1, size + 1))

    @staticmethod
    def _validate_and_index_jumps(size: int, jumps: Sequence[Jump]) -> Mapping[int, Jump]:
        indexed: dict[int, Jump] = {}
        for jump in jumps:
            if not (1 <= jump.from_cell <= size and 1 <= jump.to_cell <= size):
                raise InvalidBoardError("Jump endpoints must be inside the board")
            if jump.from_cell in indexed:
                raise InvalidBoardError(f"Only one snake or ladder may start at cell {jump.from_cell}")
            indexed[jump.from_cell] = jump
        return indexed

    def find_jump(self, cell_number: int) -> Jump | None:
        return self._jumps_by_start.get(cell_number)

    def apply_jump(self, cell_number: int) -> int:
        jump = self.find_jump(cell_number)
        return jump.to_cell if jump else cell_number


class Player:
    """Mutable token state for one participant. A new player starts on cell 1."""

    def __init__(self, name: str) -> None:
        if not name or not name.strip():
            raise ValueError("Player name is required")
        self.name = name
        self.position = 1

    def move_to(self, position: int) -> None:
        self.position = position
