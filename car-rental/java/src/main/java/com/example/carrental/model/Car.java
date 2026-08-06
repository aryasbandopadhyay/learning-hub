package com.example.carrental.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A rentable car at one location/store. THIS CLASS OWNS THE CONCURRENCY BOUNDARY.
 *
 * <p>Every car has its own lock and reservation list. Search/reserve lock only the specific car
 * being inspected, so unrelated cars can be booked in parallel, while one car can never be
 * double-booked for overlapping date ranges.
 */
public class Car {

    private final String id;
    private final String licensePlate;
    private final CarType type;
    private final String location;
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Reservation> reservations = new ArrayList<>();

    public Car(String id, String licensePlate, CarType type, String location) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.type = type;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public CarType getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    /** Package/service code must hold {@link #lock} before mutating this list. */
    public List<Reservation> getReservations() {
        return reservations;
    }

    @Override
    public String toString() {
        return type + "(" + id + ", " + location + ")";
    }
}
