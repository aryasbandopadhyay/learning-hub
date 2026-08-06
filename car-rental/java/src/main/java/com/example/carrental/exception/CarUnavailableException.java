package com.example.carrental.exception;

/** Thrown when a requested car already has a blocking overlapping reservation. */
public class CarUnavailableException extends RuntimeException {
    public CarUnavailableException(String message) {
        super(message);
    }
}
