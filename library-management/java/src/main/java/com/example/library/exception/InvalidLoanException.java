package com.example.library.exception;

/** Thrown when a loan id is unknown (e.g. already returned, or invalid). */
public class InvalidLoanException extends RuntimeException {
    public InvalidLoanException(String message) {
        super(message);
    }
}
