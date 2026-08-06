package com.example.library.exception;

/** Thrown when a member already has the maximum allowed active loans. */
public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
