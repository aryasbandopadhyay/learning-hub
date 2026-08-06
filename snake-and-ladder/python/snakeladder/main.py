"""Runnable deterministic demo. Real games can swap ScriptedDice for RandomDice."""

from __future__ import annotations

from .game import Game, MoveResult, ScriptedDice
from .models import Board, Jump, Player


def _format_turn(turn: int, result: MoveResult) -> str:
    line = (
        f"Turn {turn}: {result.player_name} rolled {result.roll} "
        f"and moved {result.from_cell} -> {result.attempted_cell}"
    )
    if result.jump:
        line += (", ladder to " if result.jump.is_ladder else ", snake to ") + str(result.jump.to_cell)
    if result.won:
        line += " and won"
    return line


def main() -> None:
    board = Board(10, [Jump.ladder(4, 8), Jump.snake(9, 5)])
    game = Game(board, ScriptedDice([3, 4, 2]), [Player("Alice"), Player("Bob")])

    turn = 1
    while game.winner is None:
        print(_format_turn(turn, game.play_turn()))
        turn += 1
    print(f"Winner: {game.winner.name}")


if __name__ == "__main__":  # pragma: no cover - demo entry point
    main()
