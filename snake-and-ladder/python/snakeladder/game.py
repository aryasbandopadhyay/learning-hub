"""Game orchestration and dice strategies."""

from __future__ import annotations

import random
from collections import deque
from dataclasses import dataclass
from typing import Protocol, Sequence

from .exceptions import GameAlreadyOverError
from .models import Board, Jump, Player


class Dice(Protocol):
    """Strategy protocol: real dice can be random; tests/demos inject scripted dice."""

    def roll(self) -> int:
        ...


class RandomDice:
    """Default 1..6 dice. Supplying a seed makes pseudo-random games repeatable."""

    def __init__(self, minimum: int = 1, maximum: int = 6, seed: int | None = None) -> None:
        if minimum > maximum:
            raise ValueError("Dice minimum cannot exceed maximum")
        self._minimum = minimum
        self._maximum = maximum
        self._random = random.Random(seed)

    def roll(self) -> int:
        return self._random.randint(self._minimum, self._maximum)


class ScriptedDice:
    """Deterministic dice for demos/tests: returns the provided values in order."""

    def __init__(self, rolls: Sequence[int]) -> None:
        if not rolls:
            raise ValueError("At least one scripted roll is required")
        self._rolls = deque(rolls)

    def roll(self) -> int:
        if not self._rolls:
            raise RuntimeError("Scripted dice has no rolls left")
        roll = self._rolls.popleft()
        if roll < 1:
            raise RuntimeError("Dice roll must be positive")
        return roll


@dataclass(frozen=True)
class MoveResult:
    """Report for one turn; useful for logs, demos, and assertions."""

    player_name: str
    roll: int
    from_cell: int
    attempted_cell: int
    to_cell: int
    jump: Jump | None
    won: bool

    @property
    def overshot(self) -> bool:
        return self.attempted_cell == self.from_cell


class Game:
    """Turn orchestrator. Board rules and dice policy stay injected.

    Overshoot rule used here: if a roll would pass the final cell, the token does not move. The
    player must reach the last cell exactly. This is documented because real variants differ.
    """

    def __init__(self, board: Board, dice: Dice, players: Sequence[Player]) -> None:
        if not players:
            raise ValueError("At least one player is required")
        self.board = board
        self.dice = dice
        self.players = tuple(players)
        self._current_player_index = 0
        self.winner: Player | None = None

    def play_turn(self) -> MoveResult:
        if self.winner is not None:
            raise GameAlreadyOverError(f"Game already has a winner: {self.winner.name}")

        player = self.players[self._current_player_index]
        from_cell = player.position
        roll = self.dice.roll()
        attempted = from_cell + roll

        jump = None
        to_cell = attempted
        if attempted > self.board.size:
            # Exact-roll-to-win MVP choice: too far means no movement at all.
            attempted = from_cell
            to_cell = from_cell
        else:
            jump = self.board.find_jump(attempted)
            to_cell = self.board.apply_jump(attempted)
            player.move_to(to_cell)

        won = to_cell == self.board.size
        if won:
            self.winner = player
        else:
            self._current_player_index = (self._current_player_index + 1) % len(self.players)
        return MoveResult(player.name, roll, from_cell, attempted, to_cell, jump, won)

    def play_to_completion(self) -> Player:
        while self.winner is None:
            self.play_turn()
        return self.winner
