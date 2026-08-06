package com.example.chess.model;

/** The two chess sides. WHITE moves toward lower row numbers; BLACK moves toward higher rows. */
public enum Color {
    WHITE,
    BLACK;

    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
