"""The Tic Tac Toe game engine."""

from __future__ import annotations

from .exceptions import InvalidMoveError
from .models import Board, GameStatus, Mark, Player


class Game:
    """Validate turns, delegate board writes, then transition game status.

    This class is intentionally single-threaded/turn-based. A caller supplies one move at a time,
    so locks would add noise without improving the correctness of the MVP.
    """

    def __init__(self, x_player: Player, o_player: Player, size: int = Board.DEFAULT_SIZE) -> None:
        if x_player.mark is not Mark.X or o_player.mark is not Mark.O:
            raise ValueError("Game requires one X player and one O player")
        self.board = Board(size)
        self._x_player = x_player
        self._o_player = o_player
        self.current_player = x_player
        self.status = GameStatus.IN_PROGRESS

    def make_move(self, player: Player, row: int, col: int) -> GameStatus:
        if self.status is not GameStatus.IN_PROGRESS:
            raise InvalidMoveError(f"Game is already over with status {self.status.value}")
        if player != self.current_player:
            raise InvalidMoveError(f"It is {self.current_player.name}'s turn")

        self.board.place_mark(row, col, player.mark)
        if self.board.has_winning_line(player.mark):
            self.status = GameStatus.X_WON if player.mark is Mark.X else GameStatus.O_WON
        elif self.board.is_full():
            self.status = GameStatus.DRAW
        else:
            self.current_player = self._o_player if self.current_player == self._x_player else self._x_player
        return self.status
