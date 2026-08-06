package com.example.chess.model.pieces;

import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;

/** Queen combines rook and bishop geometry: straight or diagonal, with a clear path. */
public class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Board board, Cell from, Cell to) {
        return !from.equals(to)
                && (isStraight(from, to) || isDiagonal(from, to))
                && board.isPathClear(from, to)
                && destinationIsNotOwnPiece(board, to);
    }
}
