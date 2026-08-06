package com.example.airline.model;

import java.time.Instant;

/** Immutable PNR record created after the seat has already been atomically marked BOOKED. */
public record Booking(String pnr,
                      String flightNumber,
                      String seatNo,
                      Passenger passenger,
                      Cabin cabin,
                      long price,
                      Instant bookedAt) {
}
