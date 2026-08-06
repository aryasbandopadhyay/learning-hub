package com.example.snakeladder.model;

import com.example.snakeladder.exception.InvalidBoardException;

/**
 * A directed edge on the board. Landing on {@code from} instantly moves the player to {@code to}.
 *
 * <p>Factory methods encode the real-world rule in their names: a snake goes down, a ladder goes up.
 */
public class Jump {

    private final int from;
    private final int to;
    private final JumpType type;

    private Jump(int from, int to, JumpType type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public static Jump snake(int head, int tail) {
        if (head <= tail) {
            throw new InvalidBoardException("Snake head must be greater than tail");
        }
        return new Jump(head, tail, JumpType.SNAKE);
    }

    public static Jump ladder(int bottom, int top) {
        if (top <= bottom) {
            throw new InvalidBoardException("Ladder top must be greater than bottom");
        }
        return new Jump(bottom, top, JumpType.LADDER);
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public JumpType getType() {
        return type;
    }

    public boolean isLadder() {
        return type == JumpType.LADDER;
    }
}
