package com.example.movieticket;

import com.example.movieticket.exception.InvalidBookingStateException;
import com.example.movieticket.exception.PaymentRejectedException;
import com.example.movieticket.exception.SeatUnavailableException;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.SeatStatus;
import com.example.movieticket.model.Show;
import com.example.movieticket.payment.AlwaysSuccessPaymentProcessor;
import com.example.movieticket.payment.FailingPaymentProcessor;
import com.example.movieticket.service.BookingService;
import com.example.movieticket.state.BookingState;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Mutable clock copied from the reference style: expiry tests advance time, never sleep. */
class MutableClock extends Clock {
    private Instant now;

    MutableClock(Instant start) {
        this.now = start;
    }

    void advance(Duration d) {
        now = now.plus(d);
    }

    @Override
    public Instant instant() { return now; }

    @Override
    public ZoneOffset getZone() { return ZoneOffset.UTC; }

    @Override
    public Clock withZone(java.time.ZoneId zone) { return this; }
}

class MovieTicketBookingTest {

    private BookingService service(Show show, Clock clock) {
        return new BookingService(List.of(show), new AlwaysSuccessPaymentProcessor(), clock, Duration.ofMinutes(5));
    }

    @Test
    void happyPathHoldsThenPaysAndBooksSeats() {
        Show show = new Show("S1", 2, 2, 200);
        BookingService service = service(show, Clock.systemUTC());

        Booking booking = service.createBooking("S1", List.of("R1C1", "R1C2"), "U1");
        assertEquals(BookingState.SEATS_HELD, booking.getState());
        assertEquals(400, booking.getTotalPrice());
        assertEquals(SeatStatus.HELD, show.getSeat("R1C1").getStatus());

        service.pay(booking.getId(), "PAY-1");
        assertEquals(BookingState.CONFIRMED, booking.getState());
        assertEquals(SeatStatus.BOOKED, show.getSeat("R1C1").getStatus());
        assertEquals(SeatStatus.BOOKED, show.getSeat("R1C2").getStatus());
    }

    @Test
    void paymentFailureMarksFailedAndReleasesSeats() {
        Show show = new Show("S1", 1, 2, 200);
        BookingService service = new BookingService(
                List.of(show), new FailingPaymentProcessor(), Clock.systemUTC(), Duration.ofMinutes(5));
        Booking booking = service.createBooking("S1", List.of("R1C1"), "U1");

        assertThrows(PaymentRejectedException.class, () -> service.pay(booking.getId(), "PAY-BAD"));

        assertEquals(BookingState.FAILED, booking.getState());
        assertEquals(SeatStatus.AVAILABLE, show.getSeat("R1C1").getStatus());
    }

    @Test
    void expiredHoldRejectsPaymentAndReleasesSeats() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T10:00:00Z"));
        Show show = new Show("S1", 1, 2, 200);
        BookingService service = service(show, clock);
        Booking booking = service.createBooking("S1", List.of("R1C1"), "U1");
        clock.advance(Duration.ofMinutes(6));

        assertThrows(InvalidBookingStateException.class, () -> service.pay(booking.getId(), "PAY-LATE"));
        assertEquals(BookingState.EXPIRED, booking.getState());
        assertEquals(SeatStatus.AVAILABLE, show.getSeat("R1C1").getStatus());
    }

    @Test
    void illegalTransitionPayingConfirmedBookingIsRejected() {
        Show show = new Show("S1", 1, 2, 200);
        BookingService service = service(show, Clock.systemUTC());
        Booking booking = service.createBooking("S1", List.of("R1C1"), "U1");
        service.pay(booking.getId(), "PAY-1");

        assertThrows(InvalidBookingStateException.class, () -> service.pay(booking.getId(), "PAY-AGAIN"));
        assertEquals(BookingState.CONFIRMED, booking.getState());
    }

    @Test
    void allOrNothingHoldLeavesOtherRequestedSeatsAvailable() {
        Show show = new Show("S1", 1, 3, 200);
        BookingService service = service(show, Clock.systemUTC());
        service.createBooking("S1", List.of("R1C1"), "U1");

        assertThrows(SeatUnavailableException.class,
                () -> service.createBooking("S1", List.of("R1C1", "R1C2"), "U2"));

        assertEquals(SeatStatus.HELD, show.getSeat("R1C1").getStatus());
        assertEquals(SeatStatus.AVAILABLE, show.getSeat("R1C2").getStatus());
    }

    @Test
    void schedulerExpiresStaleBookings() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T10:00:00Z"));
        Show show = new Show("S1", 1, 2, 200);
        BookingService service = service(show, clock);
        Booking booking = service.createBooking("S1", List.of("R1C1"), "U1");
        clock.advance(Duration.ofMinutes(6));

        assertEquals(1, service.expireStaleBookings(clock.instant()));

        assertEquals(BookingState.EXPIRED, booking.getState());
        assertEquals(SeatStatus.AVAILABLE, show.getSeat("R1C1").getStatus());
    }

    @Test
    void concurrentBookingNeverDoubleHoldsOrBooksSameSeat() throws InterruptedException {
        int threads = 50;
        Show show = new Show("S1", 1, 1, 200);
        BookingService service = service(show, Clock.systemUTC());
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> bookingIds = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await();
                    Booking booking = service.createBooking("S1", List.of("R1C1"), "U" + id);
                    service.pay(booking.getId(), "PAY-" + id);
                    successes.incrementAndGet();
                    bookingIds.add(booking.getId());
                } catch (SeatUnavailableException ignored) {
                    // expected losers of the race
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successes.get(), "exactly one booking should win the single seat");
        assertEquals(1, bookingIds.stream().distinct().count(), "winner booking id is unique");
        assertEquals(SeatStatus.BOOKED, show.getSeat("R1C1").getStatus());
    }
}
