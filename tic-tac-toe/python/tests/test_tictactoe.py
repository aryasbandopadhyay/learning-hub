"""End-to-end tests for the Tic Tac Toe MVP."""

from __future__ import annotations

import pytest

from tictactoe.exceptions import InvalidMoveError
from tictactoe.game import Game
from tictactoe.models import GameStatus, Mark, Player


X = Player("Alice", Mark.X)
O = Player("Bob", Mark.O)


def test_x_wins_on_top_row():
    game = Game(X, O)
    assert game.make_move(X, 0, 0) is GameStatus.IN_PROGRESS
    game.make_move(O, 1, 0)
    game.make_move(X, 0, 1)
    game.make_move(O, 1, 1)
    assert game.make_move(X, 0, 2) is GameStatus.X_WON


def test_x_wins_on_main_diagonal():
    game = Game(X, O)
    game.make_move(X, 0, 0)
    game.make_move(O, 0, 1)
    game.make_move(X, 1, 1)
    game.make_move(O, 0, 2)
    assert game.make_move(X, 2, 2) is GameStatus.X_WON


def test_o_wins_on_column():
    game = Game(X, O)
    game.make_move(X, 0, 0)
    game.make_move(O, 0, 1)
    game.make_move(X, 1, 0)
    game.make_move(O, 1, 1)
    game.make_move(X, 2, 2)
    assert game.make_move(O, 2, 1) is GameStatus.O_WON


def test_draw_when_board_fills_with_no_winner():
    game = Game(X, O)
    game.make_move(X, 0, 0)
    game.make_move(O, 0, 1)
    game.make_move(X, 0, 2)
    game.make_move(O, 1, 1)
    game.make_move(X, 1, 0)
    game.make_move(O, 1, 2)
    game.make_move(X, 2, 1)
    game.make_move(O, 2, 0)
    assert game.make_move(X, 2, 2) is GameStatus.DRAW


def test_invalid_moves_are_rejected_clearly():
    out_of_bounds = Game(X, O)
    with pytest.raises(InvalidMoveError):
        out_of_bounds.make_move(X, 3, 0)

    occupied = Game(X, O)
    occupied.make_move(X, 0, 0)
    with pytest.raises(InvalidMoveError):
        occupied.make_move(O, 0, 0)

    wrong_turn = Game(X, O)
    with pytest.raises(InvalidMoveError):
        wrong_turn.make_move(O, 0, 0)

    over = Game(X, O)
    over.make_move(X, 0, 0)
    over.make_move(O, 1, 0)
    over.make_move(X, 0, 1)
    over.make_move(O, 1, 1)
    over.make_move(X, 0, 2)
    with pytest.raises(InvalidMoveError):
        over.make_move(O, 2, 2)
