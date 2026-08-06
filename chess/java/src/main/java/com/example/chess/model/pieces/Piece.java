package com.example.chess.model.pieces;

import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;

/**
 * Abstract base of the chess-piece hierarchy (OOP: inheritance + polymorphism).
 *
 * <p>Every concrete piece overrides {@link #isValidMove(Board, Cell, Cell)} with its own geometry.
 * The {@code Game} service does not contain a giant switch for rook/bishop/knight/etc.; it simply
 * calls this method on the runtime piece object. That is the central LLD learning goal here.
 */
public abstract class Piece {
    private final Color color;

    protected Piece(Color color) {
        this.color = color;
    }

    /** Piece-specific movement rule. Does not check turns; Game owns turn management. */
    public abstract boolean isValidMove(Board board, Cell from, Cell to);

    protected boolean destinationIsNotOwnPiece(Board board, Cell to) {
        return !board.hasOwnPiece(to, color);
    }

    protected boolean isStraight(Cell from, Cell to) {
        return from.row() == to.row() || from.col() == to.col();
    }

    protected boolean isDiagonal(Cell from, Cell to) {
        return Math.abs(from.row() - to.row()) == Math.abs(from.col() - to.col());
    }

    public Color getColor() {
        return color;
    }

    public String symbol() {
        return getClass().getSimpleName().substring(0, 1);
    }
}
