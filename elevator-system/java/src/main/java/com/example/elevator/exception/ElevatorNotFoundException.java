package com.example.elevator.exception;

/** Raised when a car-panel request references an unknown elevator car. */
public class ElevatorNotFoundException extends RuntimeException {
    public ElevatorNotFoundException(String message) {
        super(message);
    }
}
