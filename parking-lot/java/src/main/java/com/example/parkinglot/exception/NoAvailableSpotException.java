package com.example.parkinglot.exception;

/** Thrown when no compatible free spot exists for a vehicle. */
public class NoAvailableSpotException extends RuntimeException {
    public NoAvailableSpotException(String message) {
        super(message);
    }
}
