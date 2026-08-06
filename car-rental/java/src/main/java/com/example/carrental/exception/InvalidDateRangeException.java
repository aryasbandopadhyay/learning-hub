package com.example.carrental.exception;

/** Thrown when pickup is not strictly before return. */
public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
