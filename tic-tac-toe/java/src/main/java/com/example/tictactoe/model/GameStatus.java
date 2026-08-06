package com.example.tictactoe.model;

/** Finite states of one game; terminal states reject any further move. */
public enum GameStatus {
    IN_PROGRESS,
    X_WON,
    O_WON,
    DRAW
}
