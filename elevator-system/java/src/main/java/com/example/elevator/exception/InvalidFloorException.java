package com.example.elevator.exception;

/** Raised when a request references a floor outside the building. */
public class InvalidFloorException extends RuntimeException {
    public InvalidFloorException(String message) {
        super(message);
    }
}
