package com.example.movieticket.payment;

import com.example.movieticket.model.Booking;

/** Strategy abstraction for payment: tests inject success/failure without touching BookingService. */
public interface PaymentProcessor {
    PaymentResult process(Booking booking, String paymentRef);
}
