package com.example.movieticket.payment;

import com.example.movieticket.model.Booking;

public class AlwaysSuccessPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentResult process(Booking booking, String paymentRef) {
        return PaymentResult.success("Payment accepted: " + paymentRef);
    }
}
