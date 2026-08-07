package com.example.scheduler.model;

import java.util.List;
import java.util.UUID;

/** Immutable booking returned to callers after a room has atomically accepted the interval. */
public class Booking {

    private final String id;
    private final String title;
    private final MeetingRoom room;
    private final TimeInterval interval;
    private final List<Attendee> attendees;

    public Booking(String title, MeetingRoom room, TimeInterval interval, List<Attendee> attendees) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.room = room;
        this.interval = interval;
        this.attendees = List.copyOf(attendees);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public MeetingRoom getRoom() { return room; }
    public TimeInterval getInterval() { return interval; }
    public List<Attendee> getAttendees() { return attendees; }
}
