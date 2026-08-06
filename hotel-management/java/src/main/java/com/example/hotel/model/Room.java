package com.example.hotel.model;

import com.example.hotel.exception.RoomUnavailableException;
import com.example.hotel.strategy.PricingStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A single room. THIS CLASS IS THE CONCURRENCY BOUNDARY.
 *
 * <p>The reservation list is guarded by a per-room {@link ReentrantLock}. Booking holds that lock
 * while it checks for overlapping live reservations and appends the new reservation, making the
 * check-and-insert atomic. Different rooms can still be booked in parallel.
 */
public class Room {

    private final String id;
    private final RoomType roomType;
    private final List<Reservation> reservations = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public Room(String id, RoomType roomType) {
        this.id = id;
        this.roomType = roomType;
    }

    public boolean isAvailable(LocalDate checkIn, LocalDate checkOut) {
        validateRange(checkIn, checkOut);
        lock.lock();
        try {
            return reservations.stream()
                    .noneMatch(r -> r.blocksAvailability() && r.overlaps(checkIn, checkOut));
        } finally {
            lock.unlock();
        }
    }

    /** Atomically reserve this room for [checkIn, checkOut), or throw if another live booking overlaps. */
    public Reservation book(LocalDate checkIn, LocalDate checkOut, PricingStrategy pricingStrategy) {
        validateRange(checkIn, checkOut);
        lock.lock();
        try {
            boolean blocked = reservations.stream()
                    .anyMatch(r -> r.blocksAvailability() && r.overlaps(checkIn, checkOut));
            if (blocked) {
                throw new RoomUnavailableException("Room " + id + " is unavailable for the requested dates");
            }
            long price = pricingStrategy.calculatePrice(this, checkIn, checkOut);
            Reservation reservation = new Reservation(this, checkIn, checkOut, price);
            reservations.add(reservation);
            return reservation;
        } finally {
            lock.unlock();
        }
    }

    /** Snapshot for tests/debugging without exposing the mutable internal list. */
    public List<Reservation> getReservationsSnapshot() {
        lock.lock();
        try {
            return List.copyOf(reservations);
        } finally {
            lock.unlock();
        }
    }

    private static void validateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("checkIn must be before checkOut");
        }
    }

    public String getId() {
        return id;
    }

    public RoomType getRoomType() {
        return roomType;
    }
}
