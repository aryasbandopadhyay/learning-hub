package com.example.movieticket.service;

import com.example.movieticket.exception.BookingNotFoundException;
import com.example.movieticket.exception.InvalidBookingStateException;
import com.example.movieticket.exception.PaymentRejectedException;
import com.example.movieticket.exception.SeatUnavailableException;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Seat;
import com.example.movieticket.model.SeatStatus;
import com.example.movieticket.model.Show;
import com.example.movieticket.payment.PaymentProcessor;
import com.example.movieticket.payment.PaymentResult;
import com.example.movieticket.state.BookingState;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Orchestrates the booking/payment lifecycle.
 *
 * <p>Locking rule: acquire the Show lock before touching seats. When a Booking also transitions,
 * acquire its monitor while still under the Show lock. This keeps seat inventory and booking state
 * consistent and avoids deadlocks.
 */
public class BookingService {

    private final Map<String, Show> shows = new ConcurrentHashMap<>();
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final PaymentProcessor paymentProcessor;
    private final Clock clock;
    private final Duration holdWindow;

    public BookingService(List<Show> shows, PaymentProcessor paymentProcessor, Clock clock, Duration holdWindow) {
        shows.forEach(show -> this.shows.put(show.getId(), show));
        this.paymentProcessor = paymentProcessor;
        this.clock = clock;
        this.holdWindow = holdWindow;
    }

    /** Atomically hold all requested seats or none. */
    public Booking createBooking(String showId, List<String> seatIds, String userId) {
        Show show = findShow(showId);
        show.getLock().lock();
        try {
            for (String seatId : seatIds) {
                Seat seat = show.getSeat(seatId);
                if (seat == null || seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new SeatUnavailableException("Seat not available: " + seatId);
                }
            }

            Booking booking = new Booking(
                    showId,
                    seatIds,
                    userId,
                    seatIds.size() * show.getPricePerSeat(),
                    clock.instant().plus(holdWindow));
            booking.transitionTo(BookingState.SEATS_HELD);

            for (String seatId : seatIds) {
                show.getSeat(seatId).holdFor(booking.getId());
            }
            bookings.put(booking.getId(), booking);
            return booking;
        } finally {
            show.getLock().unlock();
        }
    }

    /** Pay a held booking. Expired holds are rejected before payment and their seats are released. */
    public Booking pay(String bookingId, String paymentRef) {
        Booking booking = findBooking(bookingId);
        Show show = findShow(booking.getShowId());
        show.getLock().lock();
        try {
            synchronized (booking) {
                expireIfNeededUnderLock(show, booking, clock.instant());
                if (booking.getState() != BookingState.SEATS_HELD) {
                    throw new InvalidBookingStateException("Cannot pay booking in state " + booking.getState());
                }

                booking.transitionTo(BookingState.PAYMENT_PENDING);
                booking.setPaymentRef(paymentRef);
                PaymentResult result = paymentProcessor.process(booking, paymentRef);
                if (result.success()) {
                    for (String seatId : booking.getSeatIds()) {
                        show.getSeat(seatId).bookFor(booking.getId());
                    }
                    booking.transitionTo(BookingState.CONFIRMED);
                    return booking;
                }

                releaseSeats(show, booking);
                booking.transitionTo(BookingState.FAILED);
                throw new PaymentRejectedException(result.message());
            }
        } finally {
            show.getLock().unlock();
        }
    }

    /** Release every stale hold. Useful for a scheduler and deterministic tests with a fake clock. */
    public int expireStaleBookings(Instant now) {
        int expired = 0;
        for (Booking booking : bookings.values()) {
            Show show = findShow(booking.getShowId());
            show.getLock().lock();
            try {
                synchronized (booking) {
                    if (expireIfNeededUnderLock(show, booking, now)) {
                        expired++;
                    }
                }
            } finally {
                show.getLock().unlock();
            }
        }
        return expired;
    }

    private boolean expireIfNeededUnderLock(Show show, Booking booking, Instant now) {
        BookingState state = booking.getState();
        if ((state == BookingState.SEATS_HELD || state == BookingState.PAYMENT_PENDING)
                && now.isAfter(booking.getHoldExpiresAt())) {
            releaseSeats(show, booking);
            booking.transitionTo(BookingState.EXPIRED);
            return true;
        }
        return false;
    }

    private void releaseSeats(Show show, Booking booking) {
        for (String seatId : booking.getSeatIds()) {
            Seat seat = show.getSeat(seatId);
            if (seat != null && booking.getId().equals(seat.getBookingId())) {
                seat.release();
            }
        }
    }

    public Booking getBooking(String bookingId) {
        return findBooking(bookingId);
    }

    public Show getShow(String showId) {
        return findShow(showId);
    }

    private Booking findBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException("Unknown booking: " + bookingId);
        }
        return booking;
    }

    private Show findShow(String showId) {
        Show show = shows.get(showId);
        if (show == null) {
            throw new IllegalArgumentException("Unknown show: " + showId);
        }
        return show;
    }
}
