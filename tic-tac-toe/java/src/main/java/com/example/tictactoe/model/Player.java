package com.example.tictactoe.model;

/** Immutable player identity: a display name plus the mark this player owns. */
public record Player(String name, Mark mark) {
    public Player {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name must be non-empty");
        }
        if (mark == null || mark == Mark.EMPTY) {
            throw new IllegalArgumentException("Player mark must be X or O");
        }
    }
}
