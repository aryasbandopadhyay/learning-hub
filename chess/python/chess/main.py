"""Runnable demo: play a tiny legal opening and print the resulting turn/capture state.

Run:  python -m chess.main   (from the python/ directory)
"""

from __future__ import annotations

from .game import Game


def main() -> None:
    game = Game()
    print("Starting turn:", game.current_turn.value)

    game.make_move("e2", "e4")
    print("White plays e2 -> e4")
    game.make_move("e7", "e5")
    print("Black plays e7 -> e5")
    game.make_move("g1", "f3")
    print("White plays g1 -> f3")
    game.make_move("b8", "c6")
    print("Black plays b8 -> c6")

    print("Next turn:", game.current_turn.value)
    print("Captured pieces:", len(game.captured_pieces))


if __name__ == "__main__":
    main()
