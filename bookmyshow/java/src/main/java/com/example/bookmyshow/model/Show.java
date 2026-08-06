package com.example.bookmyshow.model;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Show is the seat-locking aggregate. One ReentrantLock protects its seat map.
 * That coarse per-show lock is deliberate: a booking often spans multiple seats,
 * so we need one critical section for all-or-nothing validation and mutation.
 */
public class Show {
    private final String id;
    private final Movie movie;
    private final Instant startTime;
    private final Map<String, Seat> seats;
    private final ReentrantLock lock = new ReentrantLock();

    public Show(String id, Movie movie, Instant startTime, List<Seat> seats) {
        this.id = id;
        this.movie = movie;
        this.startTime = startTime;
        this.seats = new LinkedHashMap<>();
        for (Seat seat : seats) {
            this.seats.put(seat.getId(), seat);
        }
    }

    public String getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public Seat getSeat(String seatId) {
        return seats.get(seatId);
    }

    public Collection<Seat> getSeats() {
        return seats.values();
    }
}
