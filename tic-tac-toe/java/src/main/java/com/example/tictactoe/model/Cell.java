package com.example.tictactoe.model;

/** A tiny mutable value object owned only by Board; Game never mutates cells directly. */
public class Cell {

    private Mark mark = Mark.EMPTY;

    public Mark getMark() {
        return mark;
    }

    public boolean isEmpty() {
        return mark == Mark.EMPTY;
    }

    public void place(Mark mark) {
        this.mark = mark;
    }
}
