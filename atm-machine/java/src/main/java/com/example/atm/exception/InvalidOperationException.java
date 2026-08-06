package com.example.atm.exception;

/** Raised when an operation is not legal in the ATM's current state. */
public class InvalidOperationException extends AtmException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
