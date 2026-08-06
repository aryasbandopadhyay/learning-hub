package com.example.airline.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Flight metadata plus its mutable seat inventory. */
public class Flight {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDateTime departureTime;
    private final FlightInventory inventory;

    public Flight(String flightNumber,
                  String origin,
                  String destination,
                  LocalDateTime departureTime,
                  FlightInventory inventory) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.inventory = inventory;
    }

    public boolean matches(String origin, String destination, LocalDate date) {
        boolean routeMatches = this.origin.equalsIgnoreCase(origin)
                && this.destination.equalsIgnoreCase(destination);
        boolean dateMatches = date == null || departureTime.toLocalDate().equals(date);
        return routeMatches && dateMatches;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public FlightInventory getInventory() {
        return inventory;
    }
}
