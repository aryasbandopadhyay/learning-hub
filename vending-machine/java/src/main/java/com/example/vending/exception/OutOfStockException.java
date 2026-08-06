package com.example.vending.exception;

/** Raised when a known product has zero stock. */
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
