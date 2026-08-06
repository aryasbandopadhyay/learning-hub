package com.example.find.exception;

/** Thrown when the in-memory tree is built with invalid structure. */
public class InvalidFileSystemException extends RuntimeException {
    public InvalidFileSystemException(String message) {
        super(message);
    }
}
