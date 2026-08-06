package com.example.hotel.model;

/**
 * Room categories with their base nightly rate. Keeping the rate on the enum makes the MVP small
 * and explicit; a production system could load these rates from a pricing catalog.
 */
public enum RoomType {
    STANDARD(100),
    DELUXE(180),
    SUITE(300);

    private final long nightlyRate;

    RoomType(long nightlyRate) {
        this.nightlyRate = nightlyRate;
    }

    public long getNightlyRate() {
        return nightlyRate;
    }
}
