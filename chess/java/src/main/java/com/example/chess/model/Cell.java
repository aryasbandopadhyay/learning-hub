package com.example.chess.model;

/** Immutable board coordinate. Rows/cols are 0-based; algebraic input is also supported. */
public record Cell(int row, int col) {

    public Cell {
        if (row < 0 || row >= Board.SIZE || col < 0 || col >= Board.SIZE) {
            throw new IllegalArgumentException("Cell outside board: " + row + "," + col);
        }
    }

    public static Cell of(int row, int col) {
        return new Cell(row, col);
    }

    public static Cell fromAlgebraic(String value) {
        if (value == null || value.length() != 2) {
            throw new IllegalArgumentException("Use algebraic notation like e2");
        }
        char file = Character.toLowerCase(value.charAt(0));
        char rank = value.charAt(1);
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Cell outside board: " + value);
        }
        return new Cell('8' - rank, file - 'a');
    }

    public String toAlgebraic() {
        return String.valueOf((char) ('a' + col)) + (char) ('8' - row);
    }
}
