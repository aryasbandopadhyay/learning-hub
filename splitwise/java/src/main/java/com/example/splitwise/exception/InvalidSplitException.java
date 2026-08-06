package com.example.splitwise.exception;

/** Raised when split inputs are incomplete, negative, or do not add up to the expense total. */
public class InvalidSplitException extends RuntimeException {
    public InvalidSplitException(String message) {
        super(message);
    }
}
