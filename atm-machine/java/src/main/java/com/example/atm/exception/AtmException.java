package com.example.atm.exception;

/** Base unchecked exception for domain failures the caller can show to the user. */
public class AtmException extends RuntimeException {
    public AtmException(String message) {
        super(message);
    }
}
