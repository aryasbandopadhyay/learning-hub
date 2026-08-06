package com.example.locker.exception;

/** Thrown when a pickup code is unknown or has already been used. */
public class InvalidPickupCodeException extends RuntimeException {
    public InvalidPickupCodeException(String message) {
        super(message);
    }
}
