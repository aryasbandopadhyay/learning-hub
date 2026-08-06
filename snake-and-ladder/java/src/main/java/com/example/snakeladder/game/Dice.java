package com.example.snakeladder.game;

/** Strategy interface: production can be random, tests/demos can be scripted and deterministic. */
public interface Dice {
    int roll();
}
