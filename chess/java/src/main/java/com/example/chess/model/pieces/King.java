package com.example.chess.model.pieces;

import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;

/** King: one square in any direction. Castling is intentionally outside this MVP. */
public class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Board board, Cell from, Cell to) {
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return !from.equals(to) && dr <= 1 && dc <= 1 && destinationIsNotOwnPiece(board, to);
    }
}
