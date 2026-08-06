package com.example.chess.model.pieces;

import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;

/** Bishop: any number of diagonal squares, with a clear path required. */
public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Board board, Cell from, Cell to) {
        return !from.equals(to)
                && isDiagonal(from, to)
                && board.isPathClear(from, to)
                && destinationIsNotOwnPiece(board, to);
    }
}
