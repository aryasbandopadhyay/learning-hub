package com.example.snakeladder.model;

import java.util.Optional;

/** One numbered square on the board; it may optionally own one outgoing snake/ladder. */
public class Cell {

    private final int number;
    private final Jump jump;

    public Cell(int number, Jump jump) {
        this.number = number;
        this.jump = jump;
    }

    public int getNumber() {
        return number;
    }

    public Optional<Jump> getJump() {
        return Optional.ofNullable(jump);
    }
}
