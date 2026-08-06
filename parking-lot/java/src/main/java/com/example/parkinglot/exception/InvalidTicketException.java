package com.example.parkinglot.exception;

/** Thrown when a ticket id is unknown (e.g. already used at exit, or invalid). */
public class InvalidTicketException extends RuntimeException {
    public InvalidTicketException(String message) {
        super(message);
    }
}
