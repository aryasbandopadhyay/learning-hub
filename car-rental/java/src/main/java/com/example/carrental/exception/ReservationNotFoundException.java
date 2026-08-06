package com.example.carrental.exception;

/** Thrown when a reservation id is unknown. */
public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
