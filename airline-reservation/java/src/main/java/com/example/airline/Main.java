package com.example.airline;

import com.example.airline.exception.SeatAlreadyBookedException;
import com.example.airline.model.Cabin;
import com.example.airline.model.Flight;
import com.example.airline.model.FlightInventory;
import com.example.airline.model.Passenger;
import com.example.airline.model.Seat;
import com.example.airline.service.AirlineReservationService;

import java.time.LocalDateTime;
import java.util.List;

/** Runnable demo: search, book, reject duplicate booking, cancel, then book again. */
public class Main {

    public static void main(String[] args) {
        AirlineReservationService service = new AirlineReservationService();
        service.addFlight(sampleFlight());

        System.out.println("Search BLR -> DEL:");
        for (Flight flight : service.searchFlights("BLR", "DEL", null, true)) {
            System.out.println(flight.getFlightNumber() + " " + flight.getOrigin() + "->"
                    + flight.getDestination() + " seats=" + flight.getInventory().availableCount());
        }

        Passenger alice = new Passenger("Alice", "alice@example.com");
        var booking = service.bookSeat("AI101", "1A", alice);
        System.out.println("Booked " + booking.pnr() + " for " + booking.passenger().name()
                + " on " + booking.seatNo() + " price=" + booking.price());

        try {
            service.bookSeat("AI101", "1A", new Passenger("Bob", "bob@example.com"));
        } catch (SeatAlreadyBookedException ex) {
            System.out.println("Second booking rejected: " + ex.getMessage());
        }

        service.cancel(booking.pnr());
        boolean freeAgain = service.searchFlights("BLR", "DEL").get(0)
                .getInventory().findSeat("1A").orElseThrow().isAvailable();
        System.out.println("Cancelled " + booking.pnr() + "; seat 1A available=" + freeAgain);

        var rebooked = service.bookSeat("AI101", "1A", new Passenger("Bob", "bob@example.com"));
        System.out.println("Rebooked " + rebooked.pnr() + " for Bob on " + rebooked.seatNo());
    }

    private static Flight sampleFlight() {
        return new Flight(
                "AI101",
                "BLR",
                "DEL",
                LocalDateTime.of(2026, 8, 5, 9, 30),
                new FlightInventory(List.of(
                        new Seat("1A", Cabin.BUSINESS),
                        new Seat("1B", Cabin.BUSINESS),
                        new Seat("12A", Cabin.ECONOMY))));
    }
}
