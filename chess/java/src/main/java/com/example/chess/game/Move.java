package com.example.chess.game;

import com.example.chess.model.Cell;

/** Immutable value object representing one requested move. */
public record Move(Cell from, Cell to) {
    public static Move of(String from, String to) {
        return new Move(Cell.fromAlgebraic(from), Cell.fromAlgebraic(to));
    }
}
