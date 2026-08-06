package com.example.carrental.model;

/** Car category plus its base daily rate. Keeping the rate here keeps the MVP small. */
public enum CarType {
    ECONOMY(40),
    SUV(70),
    LUXURY(120);

    private final long dailyRate;

    CarType(long dailyRate) {
        this.dailyRate = dailyRate;
    }

    public long getDailyRate() {
        return dailyRate;
    }
}
