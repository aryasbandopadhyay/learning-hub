package com.example.snakeladder.exception;

/** Raised if a caller tries to play after a winner has already been declared. */
public class GameAlreadyOverException extends RuntimeException {
    public GameAlreadyOverException(String message) {
        super(message);
    }
}
