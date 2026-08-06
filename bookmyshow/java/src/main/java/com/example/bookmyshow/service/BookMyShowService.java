package com.example.bookmyshow.service;

import com.example.bookmyshow.exception.HoldExpiredException;
import com.example.bookmyshow.exception.NotFoundException;
import com.example.bookmyshow.exception.SeatUnavailableException;
import com.example.bookmyshow.model.Booking;
import com.example.bookmyshow.model.City;
import com.example.bookmyshow.model.Screen;
import com.example.bookmyshow.model.Seat;
import com.example.bookmyshow.model.SeatHold;
import com.example.bookmyshow.model.SeatStatus;
import com.example.bookmyshow.model.Show;
import com.example.bookmyshow.model.Theater;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Application service for movie discovery and booking.
 *
 * Concurrency: each Show owns one lock. holdSeats/confirmBooking acquire only that show's lock,
 * release expired holds, validate every requested seat, then mutate every requested seat before
 * unlocking. Therefore no thread can observe or create a partial hold, and two threads cannot book
 * the same seat.
 */
public class BookMyShowService {
    private final Clock clock;
    private final Duration holdDuration;
    private final ConcurrentMap<String, City> cities = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Show> shows = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, SeatHold> holds = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Booking> bookings = new ConcurrentHashMap<>();

    public BookMyShowService(Clock clock, Duration holdDuration) {
        this.clock = clock;
        this.holdDuration = holdDuration;
    }

    public void addCity(City city) {
        cities.put(city.getId(), city);
        for (Theater theater : city.getTheaters()) {
            for (Screen screen : theater.getScreens()) {
                for (Show show : screen.getShows()) {
                    shows.put(show.getId(), show);
                }
            }
        }
    }

    public List<Show> searchShows(String cityName, String movieTitle) {
        String cityKey = cityName.toLowerCase(Locale.ROOT);
        String movieKey = movieTitle.toLowerCase(Locale.ROOT);
        List<Show> result = new ArrayList<>();
        for (City city : cities.values()) {
            if (!city.getName().toLowerCase(Locale.ROOT).equals(cityKey)) {
                continue;
            }
            for (Theater theater : city.getTheaters()) {
                for (Screen screen : theater.getScreens()) {
                    for (Show show : screen.getShows()) {
                        if (show.getMovie().title().toLowerCase(Locale.ROOT).equals(movieKey)) {
                            result.add(show);
                        }
                    }
                }
            }
        }
        return result;
    }

    public SeatHold holdSeats(String showId, List<String> seatIds, String userId) {
        Show show = findShow(showId);
        show.getLock().lock();
        try {
            Instant now = clock.instant();
            releaseExpiredHoldsForShow(show, now);
            for (String seatId : seatIds) {
                Seat seat = requireSeat(show, seatId);
                if (!seat.isAvailable()) {
                    throw new SeatUnavailableException("Seat not available: " + seatId);
                }
            }
            String holdId = UUID.randomUUID().toString();
            Instant expiresAt = now.plus(holdDuration);
            for (String seatId : seatIds) {
                show.getSeat(seatId).hold(holdId, userId, expiresAt);
            }
            SeatHold hold = new SeatHold(holdId, showId, seatIds, userId, expiresAt);
            holds.put(holdId, hold);
            return hold;
        } finally {
            show.getLock().unlock();
        }
    }

    public Booking confirmBooking(String holdId, String paymentRef) {
        SeatHold hold = holds.get(holdId);
        if (hold == null) {
            throw new NotFoundException("Unknown hold: " + holdId);
        }
        Show show = findShow(hold.showId());
        show.getLock().lock();
        try {
            Instant now = clock.instant();
            if (!hold.expiresAt().isAfter(now)) {
                releaseHoldSeats(show, hold);
                holds.remove(holdId);
                throw new HoldExpiredException("Hold expired: " + holdId);
            }
            for (String seatId : hold.seatIds()) {
                Seat seat = requireSeat(show, seatId);
                if (!seat.isHeldBy(holdId)) {
                    throw new SeatUnavailableException("Seat is no longer held by this hold: " + seatId);
                }
            }
            for (String seatId : hold.seatIds()) {
                show.getSeat(seatId).book();
            }
            Booking booking = new Booking(UUID.randomUUID().toString(), holdId, hold.showId(),
                    hold.seatIds(), hold.userId(), paymentRef, now);
            bookings.put(booking.id(), booking);
            holds.remove(holdId);
            return booking;
        } finally {
            show.getLock().unlock();
        }
    }

    public void releaseExpiredHolds(Instant now) {
        for (Show show : shows.values()) {
            show.getLock().lock();
            try {
                releaseExpiredHoldsForShow(show, now);
            } finally {
                show.getLock().unlock();
            }
        }
    }

    public SeatStatus seatStatus(String showId, String seatId) {
        Show show = findShow(showId);
        show.getLock().lock();
        try {
            releaseExpiredHoldsForShow(show, clock.instant());
            return requireSeat(show, seatId).getStatus();
        } finally {
            show.getLock().unlock();
        }
    }

    public Map<String, Booking> bookings() {
        return Map.copyOf(bookings);
    }

    private void releaseExpiredHoldsForShow(Show show, Instant now) {
        for (SeatHold hold : List.copyOf(holds.values())) {
            if (hold.showId().equals(show.getId()) && !hold.expiresAt().isAfter(now)) {
                releaseHoldSeats(show, hold);
                holds.remove(hold.id());
            }
        }
    }

    private void releaseHoldSeats(Show show, SeatHold hold) {
        for (String seatId : hold.seatIds()) {
            Seat seat = show.getSeat(seatId);
            if (seat != null && seat.isHeldBy(hold.id())) {
                seat.releaseHold();
            }
        }
    }

    private Show findShow(String showId) {
        Show show = shows.get(showId);
        if (show == null) {
            throw new NotFoundException("Unknown show: " + showId);
        }
        return show;
    }

    private Seat requireSeat(Show show, String seatId) {
        Seat seat = show.getSeat(seatId);
        if (seat == null) {
            throw new NotFoundException("Unknown seat: " + seatId);
        }
        return seat;
    }
}
