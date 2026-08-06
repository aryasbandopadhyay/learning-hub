package com.example.movieticket.model;

import com.example.movieticket.state.BookingState;
import com.example.movieticket.state.BookingStateMachine;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Booking aggregate: user intent + selected seats + lifecycle state.
 *
 * <p>State changes are synchronized on the Booking object. BookingService always takes Show lock
 * first, then Booking monitor, to keep a single deadlock-free order.
 */
public class Booking {

    private final String id;
    private final String showId;
    private final String userId;
    private final List<String> seatIds;
    private final long totalPrice;
    private final Instant holdExpiresAt;
    private BookingState state;
    private String paymentRef;

    public Booking(String showId, List<String> seatIds, String userId, long totalPrice, Instant holdExpiresAt) {
        this.id = UUID.randomUUID().toString();
        this.showId = showId;
        this.userId = userId;
        this.seatIds = List.copyOf(seatIds);
        this.totalPrice = totalPrice;
        this.holdExpiresAt = holdExpiresAt;
        this.state = BookingState.CREATED;
    }

    public synchronized void transitionTo(BookingState next) {
        BookingStateMachine.ensureCanMove(this.state, next);
        this.state = next;
    }

    public synchronized BookingState getState() { return state; }
    public String getId() { return id; }
    public String getShowId() { return showId; }
    public String getUserId() { return userId; }
    public List<String> getSeatIds() { return seatIds; }
    public long getTotalPrice() { return totalPrice; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }
    public synchronized String getPaymentRef() { return paymentRef; }
    public synchronized void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }
}
