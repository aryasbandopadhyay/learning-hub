package com.example.atm.exception;

/** Raised for wrong PIN attempts or when a card must be ejected after too many failures. */
public class AuthenticationException extends AtmException {
    public AuthenticationException(String message) {
        super(message);
    }
}
