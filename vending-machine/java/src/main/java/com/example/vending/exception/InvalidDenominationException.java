package com.example.vending.exception;

/** Raised when a coin is not in the machine's accepted denomination set. */
public class InvalidDenominationException extends RuntimeException {
    public InvalidDenominationException(String message) {
        super(message);
    }
}
