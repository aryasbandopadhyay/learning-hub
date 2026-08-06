package com.example.logging.exception;

/** Runtime wrapper for infrastructure failures such as a file appender write error. */
public class LoggingException extends RuntimeException {
    public LoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}
