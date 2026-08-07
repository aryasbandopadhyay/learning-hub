package com.example.scheduler.service;

import com.example.scheduler.exception.BookingNotFoundException;
import com.example.scheduler.exception.NoAvailableRoomException;
import com.example.scheduler.model.Attendee;
import com.example.scheduler.model.Booking;
import com.example.scheduler.model.MeetingRoom;
import com.example.scheduler.model.TimeInterval;
import com.example.scheduler.strategy.RoomSelectionStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Application service (aggregate root) that wires room calendars + selection strategy together.
 * It depends on the RoomSelectionStrategy abstraction, so placement policy changes without edits
 * here. Active bookings are tracked in a ConcurrentHashMap for safe cancellation by id.
 */
public class MeetingScheduler {

    private final List<MeetingRoom> rooms;
    private final RoomSelectionStrategy roomSelectionStrategy;
    private final ConcurrentMap<String, Booking> activeBookings = new ConcurrentHashMap<>();

    public MeetingScheduler(List<MeetingRoom> rooms, RoomSelectionStrategy roomSelectionStrategy) {
        this.rooms = List.copyOf(rooms);
        this.roomSelectionStrategy = roomSelectionStrategy;
    }

    /** Book a meeting into one free room, or fail clearly if every room conflicts. */
    public Booking book(String title, TimeInterval interval, List<Attendee> attendees) {
        Booking booking = roomSelectionStrategy.book(rooms, interval, title, attendees)
                .orElseThrow(() -> new NoAvailableRoomException(
                        "No available room for interval " + interval.display()));
        activeBookings.put(booking.getId(), booking);
        return booking;
    }

    /** Cancel by id. The map remove is atomic, so a booking is consumed exactly once. */
    public void cancel(String bookingId) {
        Booking booking = activeBookings.remove(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException("Unknown or already-cancelled booking: " + bookingId);
        }
        booking.getRoom().cancel(bookingId);
    }

    public List<Booking> listBookingsForRoomDay(String roomId, LocalDate day) {
        MeetingRoom room = rooms.stream()
                .filter(r -> r.getId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown room: " + roomId));
        return room.bookingsForDay(day);
    }

    public List<MeetingRoom> getRooms() { return rooms; }
}
