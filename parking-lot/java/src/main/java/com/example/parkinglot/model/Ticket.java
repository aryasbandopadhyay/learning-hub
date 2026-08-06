package com.example.parkinglot.model;

import java.time.Instant;
import java.util.UUID;

/**
 * A parking ticket issued at entry. Immutable except that it records nothing about exit — the
 * exit time and fee are computed by the service at unpark time (keeping the ticket a simple record
 * of "who parked where and when").
 */
public class Ticket {

    private final String id;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final Instant entryTime;

    public Ticket(Vehicle vehicle, ParkingSpot spot, Instant entryTime) {
        this.id = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
    }

    public String getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public Instant getEntryTime() {
        return entryTime;
    }
}
