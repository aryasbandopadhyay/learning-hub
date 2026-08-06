package com.example.carrental.model;

import java.time.LocalDate;
import java.util.UUID;

/** Booking record for one car and one half-open date range: [pickupDate, returnDate). */
public class Reservation {

    private final String id;
    private final Car car;
    private final LocalDate pickupDate;
    private final LocalDate returnDate;
    private final long totalPrice;
    private ReservationStatus status;

    public Reservation(Car car, LocalDate pickupDate, LocalDate returnDate, long totalPrice) {
        this.id = UUID.randomUUID().toString();
        this.car = car;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.totalPrice = totalPrice;
        this.status = ReservationStatus.CONFIRMED;
    }

    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        // Half-open interval overlap: start1 < end2 AND start2 < end1. Return day is free.
        return pickupDate.isBefore(otherEnd) && otherStart.isBefore(returnDate);
    }

    public boolean blocksAvailability() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.PICKED_UP;
    }

    public String getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
