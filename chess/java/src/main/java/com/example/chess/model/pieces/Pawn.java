package com.example.chess.model.pieces;

import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;

/** Pawn: forward movement, initial two-square advance, and diagonal capture. */
public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Board board, Cell from, Cell to) {
        if (from.equals(to) || !destinationIsNotOwnPiece(board, to)) {
            return false;
        }
        int direction = getColor() == Color.WHITE ? -1 : 1;
        int startRow = getColor() == Color.WHITE ? 6 : 1;
        int rowDelta = to.row() - from.row();
        int colDelta = Math.abs(to.col() - from.col());

        if (colDelta == 0 && rowDelta == direction && board.isEmpty(to)) {
            return true;
        }
        if (colDelta == 0 && from.row() == startRow && rowDelta == 2 * direction) {
            Cell between = new Cell(from.row() + direction, from.col());
            return board.isEmpty(between) && board.isEmpty(to);
        }
        return colDelta == 1 && rowDelta == direction && board.hasEnemyPiece(to, getColor());
    }
}
