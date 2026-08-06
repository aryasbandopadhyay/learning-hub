package com.example.carrental.model;

/** Reservation state machine: CONFIRMED -> PICKED_UP -> RETURNED, or CONFIRMED -> CANCELLED. */
public enum ReservationStatus {
    CONFIRMED,
    PICKED_UP,
    RETURNED,
    CANCELLED
}
