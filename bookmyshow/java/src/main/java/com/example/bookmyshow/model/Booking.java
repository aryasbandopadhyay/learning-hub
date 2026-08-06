package com.example.bookmyshow.model;

import java.time.Instant;
import java.util.List;

public record Booking(String id,
                      String holdId,
                      String showId,
                      List<String> seatIds,
                      String userId,
                      String paymentRef,
                      Instant bookedAt) {
    public Booking {
        seatIds = List.copyOf(seatIds);
    }
}
