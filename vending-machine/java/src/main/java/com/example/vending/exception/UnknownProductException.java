package com.example.vending.exception;

/** Raised when a client selects a code that is not in the catalog. */
public class UnknownProductException extends RuntimeException {
    public UnknownProductException(String message) {
        super(message);
    }
}
