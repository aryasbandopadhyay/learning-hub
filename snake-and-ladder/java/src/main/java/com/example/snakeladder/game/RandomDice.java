package com.example.snakeladder.game;

import java.util.Random;

/** Default 1..6 dice. A seed can be supplied when repeatable pseudo-random runs are useful. */
public class RandomDice implements Dice {

    private final int min;
    private final int max;
    private final Random random;

    public RandomDice() {
        this(1, 6, new Random());
    }

    public RandomDice(long seed) {
        this(1, 6, new Random(seed));
    }

    public RandomDice(int min, int max, Random random) {
        if (min > max) {
            throw new IllegalArgumentException("Dice min cannot exceed max");
        }
        this.min = min;
        this.max = max;
        this.random = random;
    }

    @Override
    public int roll() {
        return random.nextInt(max - min + 1) + min;
    }
}
