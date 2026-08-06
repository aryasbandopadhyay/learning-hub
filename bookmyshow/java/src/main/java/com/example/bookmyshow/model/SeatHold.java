package com.example.bookmyshow.model;

import java.time.Instant;
import java.util.List;

public record SeatHold(String id, String showId, List<String> seatIds, String userId, Instant expiresAt) {
    public SeatHold {
        seatIds = List.copyOf(seatIds);
    }
}
