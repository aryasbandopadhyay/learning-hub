package com.example.scheduler.exception;

/** Raised when every room conflicts with the requested interval. */
public class NoAvailableRoomException extends RuntimeException {
    public NoAvailableRoomException(String message) {
        super(message);
    }
}
