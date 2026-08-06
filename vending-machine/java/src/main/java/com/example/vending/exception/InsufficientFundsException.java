package com.example.vending.exception;

/** Raised when the current balance is lower than the selected product's price. */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
