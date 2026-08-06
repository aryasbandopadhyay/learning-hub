package com.example.bookmyshow;

import com.example.bookmyshow.exception.HoldExpiredException;
import com.example.bookmyshow.exception.SeatUnavailableException;
import com.example.bookmyshow.model.Booking;
import com.example.bookmyshow.model.City;
import com.example.bookmyshow.model.Movie;
import com.example.bookmyshow.model.Screen;
import com.example.bookmyshow.model.Seat;
import com.example.bookmyshow.model.SeatHold;
import com.example.bookmyshow.model.SeatStatus;
import com.example.bookmyshow.model.Show;
import com.example.bookmyshow.model.Theater;
import com.example.bookmyshow.service.BookMyShowService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MutableClock extends Clock {
    private Instant now;

    MutableClock(Instant start) {
        this.now = start;
    }

    void advance(Duration d) {
        now = now.plus(d);
    }

    @Override
    public Instant instant() {
        return now;
    }

    @Override
    public ZoneOffset getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(java.time.ZoneId zone) {
        return this;
    }
}

class BookMyShowTest {
    private BookMyShowService newService(Clock clock) {
        BookMyShowService service = new BookMyShowService(clock, Duration.ofMinutes(5));
        service.addCity(sampleCity());
        return service;
    }

    @Test
    void searchReturnsShowsForMovieInCity() {
        BookMyShowService service = newService(Clock.systemUTC());
        List<Show> shows = service.searchShows("Bengaluru", "Interstellar");
        assertEquals(2, shows.size());
        assertTrue(shows.stream().allMatch(s -> s.getMovie().title().equals("Interstellar")));
    }

    @Test
    void holdThenConfirmBooksSeatsAndSubsequentHoldFails() {
        BookMyShowService service = newService(Clock.systemUTC());
        SeatHold hold = service.holdSeats("show-1", List.of("A1", "A2"), "user-1");
        Booking booking = service.confirmBooking(hold.id(), "pay-1");
        assertNotNull(booking.id());
        assertEquals(SeatStatus.BOOKED, service.seatStatus("show-1", "A1"));
        assertThrows(SeatUnavailableException.class,
                () -> service.holdSeats("show-1", List.of("A1"), "user-2"));
    }

    @Test
    void allOrNothingHoldFailureLeavesAvailableSeatAvailable() {
        BookMyShowService service = newService(Clock.systemUTC());
        service.holdSeats("show-1", List.of("A1"), "user-1");
        assertThrows(SeatUnavailableException.class,
                () -> service.holdSeats("show-1", List.of("A2", "A1"), "user-2"));
        assertEquals(SeatStatus.AVAILABLE, service.seatStatus("show-1", "A2"));
    }

    @Test
    void expiredHoldCannotConfirmAndReleasesSeats() {
        MutableClock clock = new MutableClock(Instant.parse("2026-08-04T10:00:00Z"));
        BookMyShowService service = newService(clock);
        SeatHold hold = service.holdSeats("show-1", List.of("A1"), "user-1");
        clock.advance(Duration.ofMinutes(6));
        assertThrows(HoldExpiredException.class, () -> service.confirmBooking(hold.id(), "pay-late"));
        assertEquals(SeatStatus.AVAILABLE, service.seatStatus("show-1", "A1"));
    }

    @Test
    void releaseExpiredHoldsMakesSeatsAvailableAgain() {
        MutableClock clock = new MutableClock(Instant.parse("2026-08-04T10:00:00Z"));
        BookMyShowService service = newService(clock);
        service.holdSeats("show-1", List.of("A1"), "user-1");
        clock.advance(Duration.ofMinutes(6));
        service.releaseExpiredHolds(clock.instant());
        assertEquals(SeatStatus.AVAILABLE, service.seatStatus("show-1", "A1"));
    }

    @Test
    void concurrentHoldsForSameSeatExactlyOneSucceeds() throws InterruptedException {
        int threads = 50;
        BookMyShowService service = newService(Clock.systemUTC());
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> winners = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await();
                    SeatHold hold = service.holdSeats("show-1", List.of("A1"), "user-" + id);
                    successes.incrementAndGet();
                    winners.add(hold.userId());
                } catch (SeatUnavailableException ignored) {
                    // expected for the losing racers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successes.get(), "exactly one user can hold the seat");
        assertEquals(1, winners.stream().distinct().count(), "only one winner identity");
        assertEquals(SeatStatus.HELD, service.seatStatus("show-1", "A1"));
    }

    @Test
    void concurrentHoldsForDistinctSeatsAllSucceedWithoutOverlap() throws InterruptedException {
        int seats = 5;
        BookMyShowService service = newService(Clock.systemUTC());
        ExecutorService pool = Executors.newFixedThreadPool(seats);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();

        for (int i = 1; i <= seats; i++) {
            final String seatId = "A" + i;
            pool.submit(() -> {
                try {
                    start.await();
                    service.holdSeats("show-1", List.of(seatId), "user-" + seatId);
                    successes.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(seats, successes.get());
        for (int i = 1; i <= seats; i++) {
            assertEquals(SeatStatus.HELD, service.seatStatus("show-1", "A" + i));
        }
    }

    private City sampleCity() {
        Movie interstellar = new Movie("movie-1", "Interstellar");
        Movie matrix = new Movie("movie-2", "Matrix");
        Screen screen1 = new Screen("screen-1", "Audi 1");
        screen1.addShow(new Show("show-1", interstellar, Instant.parse("2026-08-05T18:00:00Z"), seats("A", 5)));
        screen1.addShow(new Show("show-2", matrix, Instant.parse("2026-08-05T21:00:00Z"), seats("B", 5)));
        Theater theater1 = new Theater("theater-1", "PVR Orion");
        theater1.addScreen(screen1);

        Screen screen2 = new Screen("screen-2", "Audi 2");
        screen2.addShow(new Show("show-3", interstellar, Instant.parse("2026-08-05T20:00:00Z"), seats("C", 5)));
        Theater theater2 = new Theater("theater-2", "INOX Garuda");
        theater2.addScreen(screen2);

        City city = new City("city-1", "Bengaluru");
        city.addTheater(theater1);
        city.addTheater(theater2);
        return city;
    }

    private List<Seat> seats(String row, int count) {
        return java.util.stream.IntStream.rangeClosed(1, count).mapToObj(i -> new Seat(row, i)).toList();
    }
}
