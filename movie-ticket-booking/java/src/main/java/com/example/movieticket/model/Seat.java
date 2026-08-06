package com.example.movieticket.model;

/**
 * One physical seat in a show. Mutations are deliberately routed through BookingService while it
 * holds the Show lock, so seat inventory and booking state move together.
 */
public class Seat {

    private final String id;
    private SeatStatus status = SeatStatus.AVAILABLE;
    private String bookingId;

    public Seat(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public SeatStatus getStatus() { return status; }
    public String getBookingId() { return bookingId; }

    public void holdFor(String bookingId) {
        this.status = SeatStatus.HELD;
        this.bookingId = bookingId;
    }

    public void bookFor(String bookingId) {
        this.status = SeatStatus.BOOKED;
        this.bookingId = bookingId;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.bookingId = null;
    }
}
