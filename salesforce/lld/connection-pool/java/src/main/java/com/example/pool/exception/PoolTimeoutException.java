package com.example.pool.exception;

/** Raised when borrow waits for the configured timeout but no resource is released. */
public class PoolTimeoutException extends RuntimeException {
    public PoolTimeoutException(String message) {
        super(message);
    }
}
