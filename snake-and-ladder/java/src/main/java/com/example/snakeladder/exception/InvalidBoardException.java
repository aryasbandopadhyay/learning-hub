package com.example.snakeladder.exception;

/** Raised when the board configuration is impossible or ambiguous. */
public class InvalidBoardException extends RuntimeException {
    public InvalidBoardException(String message) {
        super(message);
    }
}
