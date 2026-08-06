package com.example.movieticket.state;

/** Explicit booking lifecycle states used by the state machine guard. */
public enum BookingState {
    CREATED,
    SEATS_HELD,
    PAYMENT_PENDING,
    CONFIRMED,
    EXPIRED,
    FAILED
}
