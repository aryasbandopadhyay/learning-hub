package com.example.scheduler.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Half-open meeting interval: [start, end). Two intervals overlap iff each starts before the other
 * ends. This treats 10:00-11:00 and 11:00-12:00 as non-conflicting, which is the usual calendar
 * rule and the key detail for Meeting Rooms II.
 */
public record TimeInterval(LocalDateTime start, LocalDateTime end) {

    public TimeInterval {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }
    }

    public boolean overlaps(TimeInterval other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    public boolean startsOn(LocalDate day) {
        return start.toLocalDate().equals(day);
    }

    public String display() {
        return "[" + start + ", " + end + ")";
    }
}
