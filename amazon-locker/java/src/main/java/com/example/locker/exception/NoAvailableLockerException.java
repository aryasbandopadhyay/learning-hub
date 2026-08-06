package com.example.locker.exception;

/** Thrown when no compatible free locker exists for a package. */
public class NoAvailableLockerException extends RuntimeException {
    public NoAvailableLockerException(String message) {
        super(message);
    }
}
