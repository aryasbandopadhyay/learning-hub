package com.example.scheduler.service;

import com.example.scheduler.model.TimeInterval;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** Standalone calendar algorithms that do not need scheduler state. */
public final class CalendarUtils {

    private CalendarUtils() {
    }

    /**
     * Classic Meeting Rooms II sweep-line. Endpoints are half-open, so an end at 10:00 frees a room
     * before a meeting starting at 10:00 needs one.
     */
    public static int minimumRoomsRequired(List<TimeInterval> meetings) {
        if (meetings.isEmpty()) {
            return 0;
        }
        List<LocalDateTime> starts = meetings.stream().map(TimeInterval::start).sorted().toList();
        List<LocalDateTime> ends = meetings.stream().map(TimeInterval::end).sorted(Comparator.naturalOrder()).toList();

        int active = 0;
        int max = 0;
        int endIndex = 0;
        for (LocalDateTime start : starts) {
            while (endIndex < ends.size() && !start.isBefore(ends.get(endIndex))) {
                active--;
                endIndex++;
            }
            active++;
            max = Math.max(max, active);
        }
        return max;
    }
}
