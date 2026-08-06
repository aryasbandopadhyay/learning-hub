package com.example.snakeladder.game;

import com.example.snakeladder.model.Jump;

import java.util.Optional;

/** Immutable report for one turn; useful for logs, demos, and assertions. */
public class MoveResult {

    private final String playerName;
    private final int roll;
    private final int from;
    private final int attempted;
    private final int to;
    private final Jump jump;
    private final boolean won;

    public MoveResult(String playerName, int roll, int from, int attempted, int to, Jump jump, boolean won) {
        this.playerName = playerName;
        this.roll = roll;
        this.from = from;
        this.attempted = attempted;
        this.to = to;
        this.jump = jump;
        this.won = won;
    }

    public boolean overshot() {
        return attempted == from;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getRoll() {
        return roll;
    }

    public int getFrom() {
        return from;
    }

    public int getAttempted() {
        return attempted;
    }

    public int getTo() {
        return to;
    }

    public Optional<Jump> getJump() {
        return Optional.ofNullable(jump);
    }

    public boolean hasWon() {
        return won;
    }
}
