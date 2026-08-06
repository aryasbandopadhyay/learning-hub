package com.example.tictactoe.exception;

/** Thrown for any rejected move: bounds, occupied cell, wrong turn, or terminal game. */
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
