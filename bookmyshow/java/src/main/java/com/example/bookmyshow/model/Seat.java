package com.example.bookmyshow.model;

import java.time.Instant;

/**
 * A seat's mutable state is intentionally tiny and is guarded by the owning Show's lock.
 * This keeps "check all seats + mark all held/booked" atomic for multi-seat booking.
 */
public class Seat {
    private final String id;
    private final String row;
    private final int number;
    private SeatStatus status = SeatStatus.AVAILABLE;
    private String holdId;
    private String heldByUserId;
    private Instant holdExpiresAt;

    public Seat(String row, int number) {
        this.id = row + number;
        this.row = row;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public String getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public String getHoldId() {
        return holdId;
    }

    public Instant getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public boolean isHeldBy(String candidateHoldId) {
        return status == SeatStatus.HELD && holdId != null && holdId.equals(candidateHoldId);
    }

    public void hold(String newHoldId, String userId, Instant expiresAt) {
        status = SeatStatus.HELD;
        holdId = newHoldId;
        heldByUserId = userId;
        holdExpiresAt = expiresAt;
    }

    public void book() {
        status = SeatStatus.BOOKED;
        holdId = null;
        heldByUserId = null;
        holdExpiresAt = null;
    }

    public void releaseHold() {
        if (status == SeatStatus.HELD) {
            status = SeatStatus.AVAILABLE;
            holdId = null;
            heldByUserId = null;
            holdExpiresAt = null;
        }
    }
}
