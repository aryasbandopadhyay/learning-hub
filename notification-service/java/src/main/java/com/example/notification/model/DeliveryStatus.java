package com.example.notification.model;

/** Final outcome emitted to callers and observers after retry is exhausted or succeeds. */
public enum DeliveryStatus {
    SENT,
    FAILED
}
