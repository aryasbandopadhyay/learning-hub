package com.example.scheduler.exception;

/** Raised when a cancel/query operation references an unknown booking id. */
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}
