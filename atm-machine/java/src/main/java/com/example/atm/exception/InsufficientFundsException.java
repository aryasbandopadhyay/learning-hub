package com.example.atm.exception;

/** Raised when an account does not have enough balance for a withdrawal. */
public class InsufficientFundsException extends AtmException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
