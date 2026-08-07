package com.example.scheduler.strategy;

import com.example.scheduler.model.Attendee;
import com.example.scheduler.model.Booking;
import com.example.scheduler.model.MeetingRoom;
import com.example.scheduler.model.TimeInterval;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * First-available policy: scan rooms by id and claim the first calendar that accepts the interval.
 * Thread-safety comes from MeetingRoom.tryBook; even if two threads pick the same room, only one
 * synchronized overlap-check-and-insert can win.
 */
public class FirstAvailableRoomSelectionStrategy implements RoomSelectionStrategy {

    @Override
    public Optional<Booking> book(List<MeetingRoom> rooms,
                                  TimeInterval interval,
                                  String title,
                                  List<Attendee> attendees) {
        return rooms.stream()
                .sorted(Comparator.comparing(MeetingRoom::getId))
                .map(room -> new Booking(title, room, interval, attendees))
                .filter(candidate -> candidate.getRoom().tryBook(candidate))
                .findFirst();
    }
}
