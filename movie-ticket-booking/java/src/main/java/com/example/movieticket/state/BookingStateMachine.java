package com.example.movieticket.state;

import com.example.movieticket.exception.InvalidBookingStateException;

import java.util.Map;
import java.util.Set;

/** Central state-machine guard that prevents illegal jumps such as EXPIRED -> CONFIRMED. */
public final class BookingStateMachine {

    private static final Map<BookingState, Set<BookingState>> ALLOWED = Map.of(
            BookingState.CREATED, Set.of(BookingState.SEATS_HELD),
            BookingState.SEATS_HELD, Set.of(BookingState.PAYMENT_PENDING, BookingState.EXPIRED),
            BookingState.PAYMENT_PENDING, Set.of(BookingState.CONFIRMED, BookingState.FAILED, BookingState.EXPIRED),
            BookingState.CONFIRMED, Set.of(),
            BookingState.EXPIRED, Set.of(),
            BookingState.FAILED, Set.of());

    private BookingStateMachine() { }

    public static void ensureCanMove(BookingState from, BookingState to) {
        if (!ALLOWED.getOrDefault(from, Set.of()).contains(to)) {
            throw new InvalidBookingStateException("Illegal booking transition: " + from + " -> " + to);
        }
    }
}
