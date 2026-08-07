package com.example.pool.exception;

/** Raised when a caller releases a foreign resource or releases the same resource twice. */
public class InvalidResourceException extends RuntimeException {
    public InvalidResourceException(String message) {
        super(message);
    }
}
