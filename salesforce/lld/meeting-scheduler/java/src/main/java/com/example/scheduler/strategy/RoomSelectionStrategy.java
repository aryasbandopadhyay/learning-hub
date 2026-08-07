package com.example.scheduler.strategy;

import com.example.scheduler.model.Attendee;
import com.example.scheduler.model.Booking;
import com.example.scheduler.model.MeetingRoom;
import com.example.scheduler.model.TimeInterval;

import java.util.List;
import java.util.Optional;

/** Decides which room gets a meeting, and atomically reserves it. */
public interface RoomSelectionStrategy {
    Optional<Booking> book(List<MeetingRoom> rooms,
                           TimeInterval interval,
                           String title,
                           List<Attendee> attendees);
}
