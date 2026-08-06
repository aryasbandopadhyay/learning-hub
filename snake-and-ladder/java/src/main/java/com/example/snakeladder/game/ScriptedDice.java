package com.example.snakeladder.game;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/** Deterministic dice for demos/tests: returns the provided values in order. */
public class ScriptedDice implements Dice {

    private final Queue<Integer> rolls;

    public ScriptedDice(List<Integer> rolls) {
        if (rolls.isEmpty()) {
            throw new IllegalArgumentException("At least one scripted roll is required");
        }
        this.rolls = new ArrayDeque<>(rolls);
    }

    @Override
    public int roll() {
        Integer next = rolls.poll();
        if (next == null) {
            throw new IllegalStateException("Scripted dice has no rolls left");
        }
        if (next < 1) {
            throw new IllegalStateException("Dice roll must be positive");
        }
        return next;
    }
}
