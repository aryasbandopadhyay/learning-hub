package com.example.ratelimiter.exception;

/** Thrown when a limiter is configured with nonsensical limits, such as zero capacity. */
public class InvalidRateLimitConfigurationException extends RuntimeException {

    public InvalidRateLimitConfigurationException(String message) {
        super(message);
    }
}
