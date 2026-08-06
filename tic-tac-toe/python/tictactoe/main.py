"""Runnable demo: a short deterministic game where X wins the top row.

Run:  python -m tictactoe.main   (from the python/ directory)
"""

from __future__ import annotations

from .game import Game
from .models import Mark, Player


def main() -> None:
    x = Player("Alice", Mark.X)
    o = Player("Bob", Mark.O)
    game = Game(x, o)

    print("Starting Tic Tac Toe (3x3)")
    play(game, x, 0, 0)
    play(game, o, 1, 0)
    play(game, x, 0, 1)
    play(game, o, 1, 1)
    play(game, x, 0, 2)
    print("Final status:", game.status.value)
    print(game.board)


def play(game: Game, player: Player, row: int, col: int) -> None:
    print(f"{player.name} places {player.mark.value} at ({row}, {col})")
    game.make_move(player, row, col)


if __name__ == "__main__":
    main()
