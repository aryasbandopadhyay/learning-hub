package com.example.atm.exception;

/** Raised when the ATM cannot produce an exact note breakdown for the requested amount. */
public class CashDispenseException extends AtmException {
    public CashDispenseException(String message) {
        super(message);
    }
}
