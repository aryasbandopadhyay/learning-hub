package com.example.airline.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Seat inventory for one flight.
 *
 * <p>The inventory owns a fixed seat map. It does not need a coarse flight-wide lock because each
 * Seat performs the atomic compare-and-set. Scans are snapshot-style: a seat may become booked just
 * after it looked available, so booking always calls Seat.tryBook before claiming success.
 */
public class FlightInventory {

    private final Map<String, Seat> seatsByNo;

    public FlightInventory(List<Seat> seats) {
        Map<String, Seat> ordered = new LinkedHashMap<>();
        for (Seat seat : seats) {
            ordered.put(seat.getSeatNo(), seat);
        }
        this.seatsByNo = Collections.unmodifiableMap(ordered);
    }

    public Optional<Seat> findSeat(String seatNo) {
        return Optional.ofNullable(seatsByNo.get(seatNo));
    }

    public boolean hasAvailableSeat() {
        return seatsByNo.values().stream().anyMatch(Seat::isAvailable);
    }

    public List<Seat> availableSeats(Cabin cabin) {
        return seatsByNo.values().stream()
                .filter(seat -> seat.getCabin() == cabin && seat.isAvailable())
                .sorted(Comparator.comparing(Seat::getSeatNo))
                .toList();
    }

    public Optional<Seat> tryBookSeat(String seatNo, Passenger passenger) {
        Seat seat = seatsByNo.get(seatNo);
        if (seat == null || !seat.tryBook(passenger)) {
            return Optional.empty();
        }
        return Optional.of(seat);
    }

    /** Pick the first still-available seat in the requested cabin and atomically claim it. */
    public Optional<Seat> tryBookAny(Cabin cabin, Passenger passenger) {
        List<Seat> ordered = new ArrayList<>(seatsByNo.values());
        ordered.sort(Comparator.comparing(Seat::getSeatNo));
        for (Seat seat : ordered) {
            if (seat.getCabin() == cabin && seat.tryBook(passenger)) {
                return Optional.of(seat);
            }
        }
        return Optional.empty();
    }

    public long availableCount() {
        return seatsByNo.values().stream().filter(Seat::isAvailable).count();
    }

    public List<Seat> getSeats() {
        return List.copyOf(seatsByNo.values());
    }
}
