package com.example.snakeladder.model;

/** Mutable token state for one participant. A new player starts on cell 1. */
public class Player {

    private final String name;
    private int position = 1;

    public Player(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name is required");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void moveTo(int position) {
        this.position = position;
    }
}
