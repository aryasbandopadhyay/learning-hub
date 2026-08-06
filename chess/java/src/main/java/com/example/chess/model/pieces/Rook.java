package com.example.chess.model.pieces;

import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;

/** Rook: any number of squares horizontally or vertically, but never through pieces. */
public class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Board board, Cell from, Cell to) {
        return !from.equals(to)
                && isStraight(from, to)
                && board.isPathClear(from, to)
                && destinationIsNotOwnPiece(board, to);
    }
}
