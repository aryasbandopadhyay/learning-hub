package com.example.chess.exception;

/** Thrown when a move violates turn, ownership, geometry, path, or capture rules. */
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
