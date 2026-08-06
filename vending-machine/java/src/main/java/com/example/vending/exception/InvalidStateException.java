package com.example.vending.exception;

/** Raised when an operation is not valid for the machine's current State object. */
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
