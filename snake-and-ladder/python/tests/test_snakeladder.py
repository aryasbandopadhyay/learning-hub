"""End-to-end tests for the Snake & Ladder MVP with deterministic dice."""

from __future__ import annotations

import pytest

from snakeladder.exceptions import InvalidBoardError
from snakeladder.game import Game, ScriptedDice
from snakeladder.models import Board, Jump, Player


def test_ladder_moves_player_up() -> None:
    game = Game(Board(20, [Jump.ladder(4, 12)]), ScriptedDice([3]), [Player("Alice")])

    result = game.play_turn()

    assert result.from_cell == 1
    assert result.attempted_cell == 4
    assert result.to_cell == 12
    assert game.players[0].position == 12
    assert result.jump and result.jump.is_ladder


def test_snake_moves_player_down() -> None:
    game = Game(Board(20, [Jump.snake(4, 2)]), ScriptedDice([3]), [Player("Alice")])

    result = game.play_turn()

    assert result.attempted_cell == 4
    assert result.to_cell == 2
    assert game.players[0].position == 2


def test_deterministic_game_produces_expected_winner() -> None:
    game = Game(
        Board(10, [Jump.ladder(4, 8), Jump.snake(9, 5)]),
        ScriptedDice([3, 4, 2]),
        [Player("Alice"), Player("Bob")],
    )

    winner = game.play_to_completion()

    assert winner.name == "Alice"
    assert winner.position == 10


def test_overshoot_leaves_player_in_place() -> None:
    game = Game(Board(10), ScriptedDice([6, 5]), [Player("Alice")])

    game.play_turn()  # Alice: 1 -> 7
    overshoot = game.play_turn()  # Alice: 7 + 5 would pass 10

    assert overshoot.from_cell == 7
    assert overshoot.attempted_cell == 7
    assert overshoot.to_cell == 7
    assert game.players[0].position == 7
    assert game.winner is None


def test_board_rejects_duplicate_jump_starts() -> None:
    with pytest.raises(InvalidBoardError):
        Board(20, [Jump.ladder(4, 12), Jump.snake(4, 2)])
