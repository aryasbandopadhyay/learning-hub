package com.example.movieticket.payment;

import com.example.movieticket.model.Booking;

public class FailingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentResult process(Booking booking, String paymentRef) {
        return PaymentResult.failure("Payment declined: " + paymentRef);
    }
}
