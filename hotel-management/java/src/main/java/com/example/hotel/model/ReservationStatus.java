package com.example.hotel.model;

/** Reservation lifecycle states. CANCELLED and CHECKED_OUT no longer block future date searches. */
public enum ReservationStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED
}
