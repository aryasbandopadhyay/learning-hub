package com.example.scheduler.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A room owns its calendar. THIS CLASS IS THE CONCURRENCY BOUNDARY.
 *
 * <p>{@link #tryBook(Booking)} and {@link #cancel(String)} are synchronized, so the overlap scan
 * and insert/remove are atomic for this room. Multiple rooms can still be booked in parallel, but
 * no two threads can double-book the same room for overlapping intervals.
 */
public class MeetingRoom {

    private final String id;
    private final String name;
    private final int capacity;

    // Guarded by 'this' monitor (synchronized methods).
    private final List<Booking> bookings = new ArrayList<>();
    private int successfulBookings;

    public MeetingRoom(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    /** Atomically add the booking only if it does not overlap existing bookings. */
    public synchronized boolean tryBook(Booking candidate) {
        boolean conflict = bookings.stream()
                .anyMatch(existing -> existing.getInterval().overlaps(candidate.getInterval()));
        if (conflict) {
            return false;
        }
        bookings.add(candidate);
        successfulBookings++;
        return true;
    }

    /** Atomically remove a booking from this room. */
    public synchronized boolean cancel(String bookingId) {
        return bookings.removeIf(b -> b.getId().equals(bookingId));
    }

    /** Snapshot of one day's bookings, sorted by start time for deterministic queries/tests. */
    public synchronized List<Booking> bookingsForDay(LocalDate day) {
        return bookings.stream()
                .filter(b -> b.getInterval().startsOn(day))
                .sorted(Comparator.comparing(b -> b.getInterval().start()))
                .toList();
    }

    public synchronized int bookingCount() { return successfulBookings; }
    public String getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
}
