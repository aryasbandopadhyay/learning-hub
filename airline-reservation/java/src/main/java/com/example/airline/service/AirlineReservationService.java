package com.example.airline.service;

import com.example.airline.exception.BookingNotFoundException;
import com.example.airline.exception.FlightNotFoundException;
import com.example.airline.exception.NoSeatAvailableException;
import com.example.airline.exception.SeatAlreadyBookedException;
import com.example.airline.model.Booking;
import com.example.airline.model.Cabin;
import com.example.airline.model.Flight;
import com.example.airline.model.Passenger;
import com.example.airline.model.Seat;
import com.example.airline.strategy.CabinPricingStrategy;
import com.example.airline.strategy.FixedCabinPricingStrategy;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Application service / aggregate root.
 *
 * <p>Flights are read-mostly in-memory data. Bookings live in a ConcurrentHashMap so PNR lookup,
 * insert, and cancellation are thread-safe. The actual inventory race is solved lower, by
 * synchronized Seat.tryBook, rather than by serializing the whole service.
 */
public class AirlineReservationService {

    private final ConcurrentMap<String, Flight> flights = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Booking> bookings = new ConcurrentHashMap<>();
    private final CabinPricingStrategy pricingStrategy;
    private final Clock clock;

    public AirlineReservationService() {
        this(new FixedCabinPricingStrategy(), Clock.systemUTC());
    }

    public AirlineReservationService(CabinPricingStrategy pricingStrategy, Clock clock) {
        this.pricingStrategy = pricingStrategy;
        this.clock = clock;
    }

    public void addFlight(Flight flight) {
        flights.put(flight.getFlightNumber(), flight);
    }

    public List<Flight> searchFlights(String origin, String destination) {
        return searchFlights(origin, destination, null, false);
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDate date, boolean onlyWithSeats) {
        return flights.values().stream()
                .filter(flight -> flight.matches(origin, destination, date))
                .filter(flight -> !onlyWithSeats || flight.getInventory().hasAvailableSeat())
                .sorted((a, b) -> a.getDepartureTime().compareTo(b.getDepartureTime()))
                .toList();
    }

    public Booking bookSeat(String flightNo, String seatNo, Passenger passenger) {
        Flight flight = getFlightOrThrow(flightNo);
        Seat seat = flight.getInventory().findSeat(seatNo)
                .orElseThrow(() -> new NoSeatAvailableException("Unknown seat " + seatNo));
        if (!seat.tryBook(passenger)) {
            throw new SeatAlreadyBookedException("Seat " + seatNo + " is already booked");
        }
        return saveBooking(flight, seat, passenger);
    }

    public Booking bookAny(String flightNo, Cabin cabin, Passenger passenger) {
        Flight flight = getFlightOrThrow(flightNo);
        Seat seat = flight.getInventory().tryBookAny(cabin, passenger)
                .orElseThrow(() -> new NoSeatAvailableException("No available " + cabin + " seat"));
        return saveBooking(flight, seat, passenger);
    }

    /**
     * Cancel consumes a PNR exactly once. Concurrent cancels on the same PNR race on map.remove;
     * only the winner gets the Booking and frees the seat.
     */
    public Booking cancel(String pnr) {
        Booking booking = bookings.remove(pnr);
        if (booking == null) {
            throw new BookingNotFoundException("Unknown or already-cancelled PNR: " + pnr);
        }
        Flight flight = getFlightOrThrow(booking.flightNumber());
        flight.getInventory().findSeat(booking.seatNo()).ifPresent(Seat::free);
        return booking;
    }

    public Optional<Booking> findBooking(String pnr) {
        return Optional.ofNullable(bookings.get(pnr));
    }

    private Flight getFlightOrThrow(String flightNo) {
        Flight flight = flights.get(flightNo);
        if (flight == null) {
            throw new FlightNotFoundException("Unknown flight: " + flightNo);
        }
        return flight;
    }

    private Booking saveBooking(Flight flight, Seat seat, Passenger passenger) {
        Booking booking = new Booking(
                generatePnr(),
                flight.getFlightNumber(),
                seat.getSeatNo(),
                passenger,
                seat.getCabin(),
                pricingStrategy.priceFor(seat.getCabin()),
                clock.instant());
        bookings.put(booking.pnr(), booking);
        return booking;
    }

    private static String generatePnr() {
        return "PNR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
