package com.example.scheduler.exception;

/** Base unchecked exception for invalid scheduler operations. */
public class SchedulerException extends RuntimeException {
    public SchedulerException(String message) {
        super(message);
    }
}
