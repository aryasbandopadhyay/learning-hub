package com.example.carrental.exception;

/** Thrown when a lifecycle transition is not allowed. */
public class InvalidReservationStateException extends RuntimeException {
    public InvalidReservationStateException(String message) {
        super(message);
    }
}
