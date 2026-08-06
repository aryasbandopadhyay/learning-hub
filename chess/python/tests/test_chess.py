"""Deterministic tests for the Chess MVP's polymorphic movement rules."""

from __future__ import annotations

import pytest

from chess.board import Board
from chess.exceptions import InvalidMoveError
from chess.game import Game
from chess.models import Cell, Color
from chess.pieces import Bishop, King, Knight, Pawn, Queen, Rook


def c(square: str) -> Cell:
    return Cell.from_algebraic(square)


def game_with_white_piece(square: str, piece) -> Game:
    board = Board.empty()
    board.set_piece(c(square), piece)
    return Game(board, Color.WHITE)


def test_rook_moves_straight_but_not_diagonal():
    game = game_with_white_piece("a1", Rook(Color.WHITE))
    game.make_move("a1", "a4")
    assert isinstance(game.board.get_piece(c("a4")), Rook)

    illegal = game_with_white_piece("a1", Rook(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        illegal.make_move("a1", "b2")


def test_bishop_moves_diagonal_but_not_straight():
    game = game_with_white_piece("c1", Bishop(Color.WHITE))
    game.make_move("c1", "g5")
    assert isinstance(game.board.get_piece(c("g5")), Bishop)

    illegal = game_with_white_piece("c1", Bishop(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        illegal.make_move("c1", "c3")


def test_knight_moves_in_l_shape_and_can_jump():
    board = Board.empty()
    board.set_piece(c("g1"), Knight(Color.WHITE))
    board.set_piece(c("g2"), Pawn(Color.WHITE))  # would block sliders, not a knight
    game = Game(board, Color.WHITE)
    game.make_move("g1", "f3")
    assert isinstance(game.board.get_piece(c("f3")), Knight)

    illegal = game_with_white_piece("g1", Knight(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        illegal.make_move("g1", "g3")


def test_pawn_moves_forward_rejects_sideways_and_captures_diagonally():
    forward = game_with_white_piece("e2", Pawn(Color.WHITE))
    forward.make_move("e2", "e4")
    assert isinstance(forward.board.get_piece(c("e4")), Pawn)

    sideways = game_with_white_piece("e2", Pawn(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        sideways.make_move("e2", "f2")

    board = Board.empty()
    board.set_piece(c("e4"), Pawn(Color.WHITE))
    board.set_piece(c("d5"), Pawn(Color.BLACK))
    capture = Game(board, Color.WHITE)
    capture.make_move("e4", "d5")
    assert len(capture.captured_pieces) == 1


def test_king_moves_one_square_but_not_two():
    game = game_with_white_piece("e1", King(Color.WHITE))
    game.make_move("e1", "e2")
    assert isinstance(game.board.get_piece(c("e2")), King)

    illegal = game_with_white_piece("e1", King(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        illegal.make_move("e1", "e3")


def test_queen_moves_straight_and_diagonal_but_rejects_knight_shape():
    straight = game_with_white_piece("d1", Queen(Color.WHITE))
    straight.make_move("d1", "d4")

    diagonal = game_with_white_piece("d1", Queen(Color.WHITE))
    diagonal.make_move("d1", "h5")

    illegal = game_with_white_piece("d1", Queen(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        illegal.make_move("d1", "e3")


def test_sliding_pieces_cannot_move_through_blockers():
    rook_board = Board.empty()
    rook_board.set_piece(c("a1"), Rook(Color.WHITE))
    rook_board.set_piece(c("a2"), Pawn(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        Game(rook_board, Color.WHITE).make_move("a1", "a4")

    bishop_board = Board.empty()
    bishop_board.set_piece(c("c1"), Bishop(Color.WHITE))
    bishop_board.set_piece(c("d2"), Pawn(Color.BLACK))
    with pytest.raises(InvalidMoveError):
        Game(bishop_board, Color.WHITE).make_move("c1", "e3")


def test_cannot_capture_own_piece_but_enemy_capture_is_recorded():
    own = Board.empty()
    own.set_piece(c("a1"), Rook(Color.WHITE))
    own.set_piece(c("a4"), Pawn(Color.WHITE))
    with pytest.raises(InvalidMoveError):
        Game(own, Color.WHITE).make_move("a1", "a4")

    enemy = Board.empty()
    enemy.set_piece(c("a1"), Rook(Color.WHITE))
    enemy.set_piece(c("a4"), Pawn(Color.BLACK))
    game = Game(enemy, Color.WHITE)
    game.make_move("a1", "a4")
    assert len(game.captured_pieces) == 1
    assert isinstance(game.board.get_piece(c("a4")), Rook)


def test_turn_enforcement_rejects_moving_out_of_turn():
    game = Game()
    with pytest.raises(InvalidMoveError):
        game.make_move("e7", "e5")


def test_simple_check_detection_finds_attacked_king():
    board = Board.empty()
    board.set_piece(c("e1"), King(Color.WHITE))
    board.set_piece(c("e8"), Rook(Color.BLACK))
    assert Game(board, Color.WHITE).is_in_check(Color.WHITE)

    board.set_piece(c("e4"), Pawn(Color.WHITE))
    assert not Game(board, Color.WHITE).is_in_check(Color.WHITE)
