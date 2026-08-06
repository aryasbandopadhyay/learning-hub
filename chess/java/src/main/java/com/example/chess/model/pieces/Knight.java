package com.example.chess.model.pieces;

import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;

/** Knight: L-shape move. It intentionally does not ask the board whether the path is clear. */
public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Board board, Cell from, Cell to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return ((dr == 2 && dc == 1) || (dr == 1 && dc == 2))
                && destinationIsNotOwnPiece(board, to);
    }
}
