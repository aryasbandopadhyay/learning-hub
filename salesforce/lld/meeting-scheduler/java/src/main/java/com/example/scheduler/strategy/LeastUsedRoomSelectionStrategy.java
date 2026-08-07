package com.example.scheduler.strategy;

import com.example.scheduler.model.Attendee;
import com.example.scheduler.model.Booking;
import com.example.scheduler.model.MeetingRoom;
import com.example.scheduler.model.TimeInterval;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Pick the room with the fewest successful bookings so historical usage stays roughly balanced. */
public class LeastUsedRoomSelectionStrategy implements RoomSelectionStrategy {

    @Override
    public Optional<Booking> book(List<MeetingRoom> rooms,
                                  TimeInterval interval,
                                  String title,
                                  List<Attendee> attendees) {
        return rooms.stream()
                .sorted(Comparator.comparingInt(MeetingRoom::bookingCount)
                        .thenComparing(MeetingRoom::getId))
                .map(room -> new Booking(title, room, interval, attendees))
                .filter(candidate -> candidate.getRoom().tryBook(candidate))
                .findFirst();
    }
}
