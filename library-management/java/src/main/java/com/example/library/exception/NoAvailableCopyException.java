package com.example.library.exception;

/** Thrown when every copy of a requested book is currently loaned out. */
public class NoAvailableCopyException extends RuntimeException {
    public NoAvailableCopyException(String message) {
        super(message);
    }
}
