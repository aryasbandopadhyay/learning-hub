package com.example.airline.model;

/** Two-state inventory machine: AVAILABLE -> BOOKED -> AVAILABLE after cancellation. */
public enum SeatStatus {
    AVAILABLE,
    BOOKED
}
