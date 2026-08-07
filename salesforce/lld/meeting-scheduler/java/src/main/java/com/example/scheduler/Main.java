package com.example.scheduler;

import com.example.scheduler.model.Attendee;
import com.example.scheduler.model.Booking;
import com.example.scheduler.model.MeetingRoom;
import com.example.scheduler.model.TimeInterval;
import com.example.scheduler.service.CalendarUtils;
import com.example.scheduler.service.MeetingScheduler;
import com.example.scheduler.strategy.FirstAvailableRoomSelectionStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Runnable demo with deterministic data/output for quick manual verification. */
public class Main {

    public static void main(String[] args) {
        List<MeetingRoom> rooms = List.of(
                new MeetingRoom("R1", "Room-A", 4),
                new MeetingRoom("R2", "Room-B", 6),
                new MeetingRoom("R3", "Room-C", 8));
        MeetingScheduler scheduler = new MeetingScheduler(rooms, new FirstAvailableRoomSelectionStrategy());

        TimeInterval planning = interval(9, 0, 10, 0);
        TimeInterval standup = interval(9, 30, 10, 0);
        List<Attendee> attendees = List.of(new Attendee("alice@example.com"), new Attendee("bob@example.com"));

        Booking b1 = scheduler.book("Planning", planning, attendees);
        Booking b2 = scheduler.book("Standup", standup, attendees);

        System.out.println("Rooms at open: " + rooms.size());
        System.out.println("Booked Planning in " + b1.getRoom().getName() + " " + b1.getInterval().display());
        System.out.println("Booked Standup  in " + b2.getRoom().getName() + " " + b2.getInterval().display());
        System.out.println("Bookings in Room-A on 2024-01-01: "
                + scheduler.listBookingsForRoomDay("R1", LocalDate.of(2024, 1, 1)).size());
        System.out.println("Minimum rooms needed for sample: "
                + CalendarUtils.minimumRoomsRequired(List.of(planning, standup, interval(10, 0, 11, 0))));
        scheduler.cancel(b1.getId());
        System.out.println("Cancelled Planning; Room-A bookings now: "
                + scheduler.listBookingsForRoomDay("R1", LocalDate.of(2024, 1, 1)).size());
    }

    private static TimeInterval interval(int sh, int sm, int eh, int em) {
        return new TimeInterval(
                LocalDateTime.of(2024, 1, 1, sh, sm),
                LocalDateTime.of(2024, 1, 1, eh, em));
    }
}
