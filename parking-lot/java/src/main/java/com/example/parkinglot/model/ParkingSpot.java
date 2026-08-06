package com.example.parkinglot.model;

/**
 * A single parking spot. THIS CLASS IS THE CONCURRENCY BOUNDARY.
 *
 * <p>{@link #tryOccupy(Vehicle)} and {@link #free()} are {@code synchronized}, so the check
 * "is this spot free and does the vehicle fit?" and the subsequent state change happen as one
 * atomic step. When many threads race for the last free spot, the monitor guarantees exactly one
 * thread flips {@code occupied} from false to true — the others see it already taken and move on.
 * This is a classic compare-and-set guarded by the object monitor.
 */
public class ParkingSpot {

    private final String id;
    private final SpotType spotType;

    // Guarded by 'this' monitor (synchronized methods).
    private boolean occupied;
    private Vehicle vehicle;

    public ParkingSpot(String id, SpotType spotType) {
        this.id = id;
        this.spotType = spotType;
    }

    /** A vehicle fits if the spot is at least as large as the vehicle requires. */
    public boolean canFit(Vehicle v) {
        return spotType.ordinal() >= v.requiredSize().ordinal();
    }

    /**
     * Atomically park the vehicle here if the spot is free and the vehicle fits.
     *
     * @return true if this thread successfully claimed the spot; false otherwise.
     */
    public synchronized boolean tryOccupy(Vehicle v) {
        if (occupied || !canFit(v)) {
            return false;
        }
        this.occupied = true;
        this.vehicle = v;
        return true;
    }

    /** Atomically release the spot. */
    public synchronized void free() {
        this.occupied = false;
        this.vehicle = null;
    }

    public synchronized boolean isOccupied() {
        return occupied;
    }

    public String getId() {
        return id;
    }

    public SpotType getSpotType() {
        return spotType;
    }
}
