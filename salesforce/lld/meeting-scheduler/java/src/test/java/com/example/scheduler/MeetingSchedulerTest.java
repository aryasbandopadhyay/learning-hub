package com.example.scheduler;

import com.example.scheduler.exception.NoAvailableRoomException;
import com.example.scheduler.model.Attendee;
import com.example.scheduler.model.Booking;
import com.example.scheduler.model.MeetingRoom;
import com.example.scheduler.model.TimeInterval;
import com.example.scheduler.service.CalendarUtils;
import com.example.scheduler.service.MeetingScheduler;
import com.example.scheduler.strategy.FirstAvailableRoomSelectionStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeetingSchedulerTest {

    private static TimeInterval interval(int sh, int sm, int eh, int em) {
        return new TimeInterval(
                LocalDateTime.of(2024, 1, 1, sh, sm),
                LocalDateTime.of(2024, 1, 1, eh, em));
    }

    private MeetingScheduler newScheduler(int roomCount) {
        return new MeetingScheduler(
                java.util.stream.IntStream.rangeClosed(1, roomCount)
                        .mapToObj(i -> new MeetingRoom("R" + i, "Room-" + i, 4))
                        .toList(),
                new FirstAvailableRoomSelectionStrategy());
    }

    @Test
    void bookingSuccessAllocatesFirstFreeRoom() {
        MeetingScheduler scheduler = newScheduler(2);
        Booking booking = scheduler.book("Planning", interval(9, 0, 10, 0), List.of(new Attendee("a@example.com")));

        assertEquals("R1", booking.getRoom().getId());
        assertEquals(1, scheduler.listBookingsForRoomDay("R1", LocalDate.of(2024, 1, 1)).size());
    }

    @Test
    void overlapRejectedWhenNoRoomIsFree() {
        MeetingScheduler scheduler = newScheduler(1);
        scheduler.book("Planning", interval(9, 0, 10, 0), List.of(new Attendee("a@example.com")));

        assertThrows(NoAvailableRoomException.class,
                () -> scheduler.book("Conflict", interval(9, 30, 10, 30), List.of(new Attendee("b@example.com"))));
    }

    @Test
    void cancelFreesRoomForSameInterval() {
        MeetingScheduler scheduler = newScheduler(1);
        Booking booking = scheduler.book("Planning", interval(9, 0, 10, 0), List.of(new Attendee("a@example.com")));
        scheduler.cancel(booking.getId());

        Booking replacement = scheduler.book("Replacement", interval(9, 0, 10, 0), List.of(new Attendee("b@example.com")));
        assertEquals("R1", replacement.getRoom().getId());
        assertEquals(1, scheduler.listBookingsForRoomDay("R1", LocalDate.of(2024, 1, 1)).size());
    }

    @Test
    void minimumRoomsUtilityUsesHalfOpenSweepLine() {
        List<TimeInterval> meetings = List.of(interval(9, 0, 10, 0), interval(9, 30, 11, 0), interval(10, 0, 10, 30), interval(11, 0, 12, 0));

        assertEquals(2, CalendarUtils.minimumRoomsRequired(meetings));
        assertEquals(0, CalendarUtils.minimumRoomsRequired(List.of()));
    }

    /** Many threads request the exact same slot; exactly room-count winners may book it. */
    @Test
    void concurrentBookingNeverDoubleBooksSameRoom() throws InterruptedException {
        int rooms = 5;
        int threads = 50;
        MeetingScheduler scheduler = newScheduler(rooms);
        TimeInterval slot = interval(9, 0, 10, 0);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> roomIds = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await(); // release all threads together to maximize contention
                    Booking booking = scheduler.book("Race" + id, slot, List.of(new Attendee("u" + id + "@example.com")));
                    successes.incrementAndGet();
                    roomIds.add(booking.getRoom().getId());
                } catch (NoAvailableRoomException ignored) {
                    // expected for the losers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(rooms, successes.get(), "exactly one booking per room should succeed");
        assertEquals(rooms, roomIds.stream().distinct().count(), "no room may be double-booked");
    }
}
