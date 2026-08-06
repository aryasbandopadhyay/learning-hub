package com.example.scheduler.exception;

/** Thrown when a schedule request is impossible, for example a non-positive interval. */
public class InvalidScheduleException extends SchedulerException {
    public InvalidScheduleException(String message) {
        super(message);
    }
}
