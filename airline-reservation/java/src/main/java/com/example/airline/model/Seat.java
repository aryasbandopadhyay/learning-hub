package com.example.airline.model;

/**
 * A single aircraft seat. THIS CLASS IS THE CONCURRENCY BOUNDARY.
 *
 * <p>{@link #tryBook(Passenger)} and {@link #free()} are synchronized, so the check "is this seat
 * available?" and the state change to BOOKED happen as one atomic step. If 50 threads race for
 * 12A, exactly one thread can flip AVAILABLE -> BOOKED; every other thread observes BOOKED and is
 * rejected. This mirrors ParkingSpot.tryOccupy in the reference project.
 */
public class Seat {

    private final String seatNo;
    private final Cabin cabin;

    // Guarded by 'this' monitor. Never read/write directly outside synchronized methods.
    private SeatStatus status = SeatStatus.AVAILABLE;
    private Passenger passenger;

    public Seat(String seatNo, Cabin cabin) {
        this.seatNo = seatNo;
        this.cabin = cabin;
    }

    /** Atomically claim this seat for the passenger; return true only for the winning thread. */
    public synchronized boolean tryBook(Passenger passenger) {
        if (status == SeatStatus.BOOKED) {
            return false;
        }
        this.status = SeatStatus.BOOKED;
        this.passenger = passenger;
        return true;
    }

    /** Atomically release the seat during cancellation. */
    public synchronized void free() {
        this.status = SeatStatus.AVAILABLE;
        this.passenger = null;
    }

    public synchronized boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public synchronized SeatStatus getStatus() {
        return status;
    }

    public synchronized Passenger getPassenger() {
        return passenger;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public Cabin getCabin() {
        return cabin;
    }
}
