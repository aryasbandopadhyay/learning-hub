package com.example.notification.exception;

/** Channel implementations throw this for transient/permanent provider failures. */
public class NotificationDeliveryException extends RuntimeException {

    public NotificationDeliveryException(String message) {
        super(message);
    }
}
