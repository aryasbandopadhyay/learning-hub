package com.example.hotel.model;

import com.example.hotel.exception.InvalidReservationStateException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * A booking for one room and a half-open date range [checkIn, checkOut). The checkout day is free,
 * which is why adjacent ranges such as Jan 1-3 and Jan 3-5 do not overlap.
 */
public class Reservation {

    private final String id;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final long totalPrice;
    private ReservationStatus status;

    public Reservation(Room room, LocalDate checkIn, LocalDate checkOut, long totalPrice) {
        this.id = UUID.randomUUID().toString();
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = ReservationStatus.CONFIRMED;
    }

    /** The canonical date rule: overlap iff start1 < end2 AND start2 < end1. */
    public boolean overlaps(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        return checkIn.isBefore(otherCheckOut) && otherCheckIn.isBefore(checkOut);
    }

    /** Only live stays block inventory. Cancelled/checked-out reservations are history. */
    public synchronized boolean blocksAvailability() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.CHECKED_IN;
    }

    public synchronized void checkIn() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationStateException("Only CONFIRMED reservations can check in");
        }
        status = ReservationStatus.CHECKED_IN;
    }

    public synchronized void checkOut() {
        if (status != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationStateException("Only CHECKED_IN reservations can check out");
        }
        status = ReservationStatus.CHECKED_OUT;
    }

    public synchronized void cancel() {
        if (status == ReservationStatus.CHECKED_OUT) {
            throw new InvalidReservationStateException("CHECKED_OUT reservations cannot be cancelled");
        }
        status = ReservationStatus.CANCELLED;
    }

    public String getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public synchronized ReservationStatus getStatus() {
        return status;
    }
}
