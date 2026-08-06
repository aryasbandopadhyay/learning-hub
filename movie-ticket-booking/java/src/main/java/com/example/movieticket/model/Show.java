package com.example.movieticket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A single movie show with a fixed seat grid and one price per seat.
 *
 * <p>The lock is the inventory boundary: create/pay/expire lock the show before checking or
 * changing seats, which makes "are all seats available? then hold them" atomic.
 */
public class Show {

    private final String id;
    private final int rows;
    private final int cols;
    private final long pricePerSeat;
    private final Map<String, Seat> seats;
    private final ReentrantLock lock = new ReentrantLock();

    public Show(String id, int rows, int cols, long pricePerSeat) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.pricePerSeat = pricePerSeat;
        this.seats = new LinkedHashMap<>();
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                String seatId = "R" + r + "C" + c;
                seats.put(seatId, new Seat(seatId));
            }
        }
    }

    public String getId() { return id; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public long getPricePerSeat() { return pricePerSeat; }
    public ReentrantLock getLock() { return lock; }

    public Seat getSeat(String seatId) {
        return seats.get(seatId);
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(new ArrayList<>(seats.values()));
    }
}
