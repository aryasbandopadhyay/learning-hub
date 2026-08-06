package com.example.movieticket.model;

/** Seat-level inventory state. Booking state is separate and lives in the Booking aggregate. */
public enum SeatStatus {
    AVAILABLE,
    HELD,
    BOOKED
}
