package com.example.airline.exception;

public class NoSeatAvailableException extends RuntimeException {
    public NoSeatAvailableException(String message) {
        super(message);
    }
}
