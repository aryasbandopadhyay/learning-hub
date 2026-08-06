"""8x8 board storage and board-level chess helpers."""

from __future__ import annotations

from .models import Cell, Color
from .pieces import Bishop, King, Knight, Pawn, Piece, Queen, Rook


class Board:
    """Stores pieces and answers board questions such as path-clear and check.

    The board knows where pieces are. It does not decide whose turn it is; that orchestration belongs
    to ``Game``. This keeps the model rules focused and testable.
    """

    SIZE = 8

    def __init__(self, setup: bool = True) -> None:
        self._grid: list[list[Piece | None]] = [[None for _ in range(self.SIZE)] for _ in range(self.SIZE)]
        if setup:
            self._setup_initial_position()

    @classmethod
    def empty(cls) -> "Board":
        return cls(setup=False)

    def get_piece(self, cell: Cell) -> Piece | None:
        return self._grid[cell.row][cell.col]

    def set_piece(self, cell: Cell, piece: Piece | None) -> None:
        self._grid[cell.row][cell.col] = piece

    def remove_piece(self, cell: Cell) -> Piece | None:
        piece = self.get_piece(cell)
        self.set_piece(cell, None)
        return piece

    def move_piece(self, from_cell: Cell, to_cell: Cell) -> None:
        piece = self.remove_piece(from_cell)
        self.set_piece(to_cell, piece)

    def is_empty(self, cell: Cell) -> bool:
        return self.get_piece(cell) is None

    def has_own_piece(self, cell: Cell, color: Color) -> bool:
        piece = self.get_piece(cell)
        return piece is not None and piece.color is color

    def has_enemy_piece(self, cell: Cell, color: Color) -> bool:
        piece = self.get_piece(cell)
        return piece is not None and piece.color is not color

    def is_path_clear(self, from_cell: Cell, to_cell: Cell) -> bool:
        """True when all squares strictly between the two cells are empty."""
        row_step = (to_cell.row > from_cell.row) - (to_cell.row < from_cell.row)
        col_step = (to_cell.col > from_cell.col) - (to_cell.col < from_cell.col)
        row = from_cell.row + row_step
        col = from_cell.col + col_step
        while row != to_cell.row or col != to_cell.col:
            if self._grid[row][col] is not None:
                return False
            row += row_step
            col += col_step
        return True

    def is_in_check(self, color: Color) -> bool:
        king_cell = self._find_king(color)
        if king_cell is None:
            raise ValueError(f"No king for {color.value}")
        return self.is_square_attacked(king_cell, color.opposite)

    def is_square_attacked(self, target: Cell, by_color: Color) -> bool:
        for row in range(self.SIZE):
            for col in range(self.SIZE):
                piece = self._grid[row][col]
                if piece is not None and piece.color is by_color and piece.is_valid_move(self, Cell(row, col), target):
                    return True
        return False

    def _find_king(self, color: Color) -> Cell | None:
        for row in range(self.SIZE):
            for col in range(self.SIZE):
                piece = self._grid[row][col]
                if isinstance(piece, King) and piece.color is color:
                    return Cell(row, col)
        return None

    def _setup_initial_position(self) -> None:
        self._set_back_rank(0, Color.BLACK)
        self._set_pawns(1, Color.BLACK)
        self._set_pawns(6, Color.WHITE)
        self._set_back_rank(7, Color.WHITE)

    def _set_pawns(self, row: int, color: Color) -> None:
        for col in range(self.SIZE):
            self._grid[row][col] = Pawn(color)

    def _set_back_rank(self, row: int, color: Color) -> None:
        self._grid[row][0] = Rook(color)
        self._grid[row][1] = Knight(color)
        self._grid[row][2] = Bishop(color)
        self._grid[row][3] = Queen(color)
        self._grid[row][4] = King(color)
        self._grid[row][5] = Bishop(color)
        self._grid[row][6] = Knight(color)
        self._grid[row][7] = Rook(color)
