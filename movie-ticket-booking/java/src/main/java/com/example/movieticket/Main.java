package com.example.movieticket;

import com.example.movieticket.exception.PaymentRejectedException;
import com.example.movieticket.exception.SeatUnavailableException;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.SeatStatus;
import com.example.movieticket.model.Show;
import com.example.movieticket.payment.AlwaysSuccessPaymentProcessor;
import com.example.movieticket.payment.FailingPaymentProcessor;
import com.example.movieticket.service.BookingService;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

/** Runnable demo for the booking state machine: hold -> pay -> confirm/fail. */
public class Main {

    public static void main(String[] args) {
        Show show = new Show("SHOW-1", 2, 3, 250);
        BookingService service = new BookingService(
                List.of(show),
                new AlwaysSuccessPaymentProcessor(),
                Clock.systemUTC(),
                Duration.ofMinutes(5));

        System.out.println("Seats at open: " + show.getSeats().size());

        Booking happy = service.createBooking("SHOW-1", List.of("R1C1", "R1C2"), "user-1");
        System.out.println("Created booking " + happy.getId() + " -> " + happy.getState()
                + ", total = " + happy.getTotalPrice());
        service.pay(happy.getId(), "PAY-OK");
        System.out.println("After payment -> " + happy.getState()
                + ", R1C1 = " + show.getSeat("R1C1").getStatus());

        BookingService failingService = new BookingService(
                List.of(new Show("SHOW-2", 1, 2, 250)),
                new FailingPaymentProcessor(),
                Clock.systemUTC(),
                Duration.ofMinutes(5));
        Booking failed = failingService.createBooking("SHOW-2", List.of("R1C1"), "user-2");
        try {
            failingService.pay(failed.getId(), "PAY-NO");
        } catch (PaymentRejectedException e) {
            System.out.println("Failed payment -> " + failed.getState()
                    + ", R1C1 = " + failingService.getShow("SHOW-2").getSeat("R1C1").getStatus());
        }

        try {
            service.createBooking("SHOW-1", List.of("R1C1"), "user-3");
        } catch (SeatUnavailableException e) {
            System.out.println("Booked seat cannot be held again -> " + SeatStatus.BOOKED);
        }
    }
}
