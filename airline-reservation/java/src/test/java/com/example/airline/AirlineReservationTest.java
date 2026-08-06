package com.example.airline;

import com.example.airline.exception.BookingNotFoundException;
import com.example.airline.exception.NoSeatAvailableException;
import com.example.airline.exception.SeatAlreadyBookedException;
import com.example.airline.model.Booking;
import com.example.airline.model.Cabin;
import com.example.airline.model.Flight;
import com.example.airline.model.FlightInventory;
import com.example.airline.model.Passenger;
import com.example.airline.model.Seat;
import com.example.airline.model.SeatStatus;
import com.example.airline.service.AirlineReservationService;
import com.example.airline.strategy.FixedCabinPricingStrategy;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** End-to-end tests for the Airline Reservation MVP, including the inventory race test. */
class AirlineReservationTest {

    private AirlineReservationService newService() {
        AirlineReservationService service = new AirlineReservationService(
                new FixedCabinPricingStrategy(),
                Clock.fixed(java.time.Instant.parse("2026-08-04T10:00:00Z"), ZoneOffset.UTC));
        service.addFlight(flight("AI101", "BLR", "DEL", LocalDateTime.of(2026, 8, 5, 9, 30), 2, 2));
        service.addFlight(flight("AI202", "BLR", "DEL", LocalDateTime.of(2026, 8, 6, 11, 0), 1, 0));
        service.addFlight(flight("AI303", "DEL", "BOM", LocalDateTime.of(2026, 8, 5, 18, 0), 1, 1));
        return service;
    }

    @Test
    void searchReturnsRouteAndCanFilterByDateAndAvailability() {
        AirlineReservationService service = newService();
        assertEquals(2, service.searchFlights("BLR", "DEL").size());
        assertEquals("AI101", service.searchFlights("BLR", "DEL", LocalDate.of(2026, 8, 5), true)
                .get(0).getFlightNumber());

        service.bookAny("AI202", Cabin.ECONOMY, new Passenger("Only", "only@example.com"));
        assertTrue(service.searchFlights("BLR", "DEL", LocalDate.of(2026, 8, 6), true).isEmpty());
        assertFalse(service.searchFlights("BLR", "DEL", LocalDate.of(2026, 8, 6), false).isEmpty());
    }

    @Test
    void bookSpecificSeatCreatesPnrAndRejectsSecondBooking() {
        AirlineReservationService service = newService();
        Booking booking = service.bookSeat("AI101", "1A", new Passenger("Alice", "a@example.com"));

        assertNotNull(booking.pnr());
        assertEquals("1A", booking.seatNo());
        assertEquals(SeatStatus.BOOKED, service.searchFlights("BLR", "DEL").get(0)
                .getInventory().findSeat("1A").orElseThrow().getStatus());
        assertThrows(SeatAlreadyBookedException.class,
                () -> service.bookSeat("AI101", "1A", new Passenger("Bob", "b@example.com")));
    }

    @Test
    void bookAnyPicksRequestedCabinAndRejectsWhenCabinFull() {
        AirlineReservationService service = newService();
        Booking first = service.bookAny("AI101", Cabin.BUSINESS, new Passenger("Biz1", "b1@example.com"));
        Booking second = service.bookAny("AI101", Cabin.BUSINESS, new Passenger("Biz2", "b2@example.com"));

        assertEquals(Cabin.BUSINESS, first.cabin());
        assertEquals(Cabin.BUSINESS, second.cabin());
        assertThrows(NoSeatAvailableException.class,
                () -> service.bookAny("AI101", Cabin.BUSINESS, new Passenger("Biz3", "b3@example.com")));
    }

    @Test
    void cancelFreesSeatAndPnrCannotBeCancelledTwice() {
        AirlineReservationService service = newService();
        Booking booking = service.bookSeat("AI101", "12A", new Passenger("Alice", "a@example.com"));

        service.cancel(booking.pnr());
        assertTrue(service.searchFlights("BLR", "DEL").get(0)
                .getInventory().findSeat("12A").orElseThrow().isAvailable());
        Booking rebooked = service.bookSeat("AI101", "12A", new Passenger("Bob", "b@example.com"));
        assertEquals("12A", rebooked.seatNo());
        assertThrows(BookingNotFoundException.class, () -> service.cancel(booking.pnr()));
    }

    /** 50 threads race for the same physical seat; exactly one PNR may be created. */
    @Test
    void concurrentSpecificSeatBookingNeverDoubleBooks() throws InterruptedException {
        AirlineReservationService service = new AirlineReservationService();
        service.addFlight(flight("AI999", "BLR", "DEL", LocalDateTime.of(2026, 8, 5, 9, 30), 1, 0));

        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> pnrs = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await();
                    Booking booking = service.bookSeat("AI999", "12A",
                            new Passenger("P" + id, "p" + id + "@example.com"));
                    successes.incrementAndGet();
                    pnrs.add(booking.pnr());
                } catch (SeatAlreadyBookedException ignored) {
                    // expected for the losers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successes.get(), "only one passenger can own seat 12A");
        assertEquals(1, pnrs.stream().distinct().count(), "only one unique PNR should exist");
        assertEquals(0, service.searchFlights("BLR", "DEL").get(0).getInventory().availableCount());
    }

    private static Flight flight(String no, String origin, String dest,
                                 LocalDateTime departure, int economy, int business) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < business; i++) {
            seats.add(new Seat("1" + (char) ('A' + i), Cabin.BUSINESS));
        }
        for (int i = 0; i < economy; i++) {
            seats.add(new Seat("12" + (char) ('A' + i), Cabin.ECONOMY));
        }
        return new Flight(no, origin, dest, departure, new FlightInventory(seats));
    }
}
