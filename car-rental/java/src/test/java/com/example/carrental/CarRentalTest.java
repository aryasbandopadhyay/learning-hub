package com.example.carrental;

import com.example.carrental.exception.CarUnavailableException;
import com.example.carrental.model.Car;
import com.example.carrental.model.CarType;
import com.example.carrental.model.Reservation;
import com.example.carrental.model.ReservationStatus;
import com.example.carrental.service.RentalCompany;
import com.example.carrental.strategy.DailyRatePricingStrategy;
import com.example.carrental.strategy.PricingStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** End-to-end tests for the Car Rental MVP, including the date-range concurrency race. */
class CarRentalTest {

    private static final LocalDate SEP_1 = LocalDate.of(2026, 9, 1);
    private static final LocalDate SEP_4 = LocalDate.of(2026, 9, 4);

    private RentalCompany newCompany(PricingStrategy strategy) {
        return new RentalCompany(List.of(
                new Car("E1", "KA-01-1111", CarType.ECONOMY, "BLR"),
                new Car("S1", "KA-02-2222", CarType.SUV, "BLR"),
                new Car("S2", "KA-03-3333", CarType.SUV, "BLR"),
                new Car("L1", "DL-04-4444", CarType.LUXURY, "DEL")),
                strategy);
    }

    @Test
    void searchExcludesOverlappingReservationsButAllowsAdjacentRanges() {
        RentalCompany company = newCompany(new DailyRatePricingStrategy());
        company.reserve("S1", SEP_1, SEP_4);

        List<Car> overlapping = company.searchAvailable("BLR", CarType.SUV,
                LocalDate.of(2026, 9, 3), LocalDate.of(2026, 9, 5));
        assertEquals(List.of("S2"), overlapping.stream().map(Car::getId).toList());

        List<Car> adjacent = company.searchAvailable("BLR", CarType.SUV,
                SEP_4, LocalDate.of(2026, 9, 6));
        assertEquals(2, adjacent.size(), "return day is free for the next pickup");
    }

    @Test
    void reserveComputesTotalAndRejectsUnavailableCar() {
        RentalCompany company = newCompany(new DailyRatePricingStrategy());
        Reservation r = company.reserve("S1", SEP_1, SEP_4);

        assertEquals(3 * CarType.SUV.getDailyRate(), r.getTotalPrice());
        assertEquals(ReservationStatus.CONFIRMED, r.getStatus());
        assertThrows(CarUnavailableException.class,
                () -> company.reserve("S1", LocalDate.of(2026, 9, 2), SEP_4));
    }

    @Test
    void lifecycleAndCancelFreesTheRange() {
        RentalCompany company = newCompany(new DailyRatePricingStrategy());
        Reservation r = company.reserve("E1", SEP_1, SEP_4);

        company.pickUp(r.getId());
        assertEquals(ReservationStatus.PICKED_UP, r.getStatus());
        company.returnCar(r.getId());
        assertEquals(ReservationStatus.RETURNED, r.getStatus());

        Reservation cancelled = company.reserve("S1", SEP_1, SEP_4);
        company.cancel(cancelled.getId());
        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
        assertEquals("S1", company.reserve("S1", SEP_1, SEP_4).getCar().getId());
    }

    @Test
    void pricingStrategyIsSwappable() {
        RentalCompany defaultCompany = newCompany(new DailyRatePricingStrategy());
        RentalCompany customCompany = newCompany((car, pickup, drop) -> 999);

        assertEquals(3 * CarType.LUXURY.getDailyRate(),
                defaultCompany.reserve("L1", SEP_1, SEP_4).getTotalPrice());
        assertEquals(999, customCompany.reserve("L1", SEP_1, SEP_4).getTotalPrice());
    }

    /**
     * Concurrency centerpiece: many threads reserve the same car for the same overlapping range.
     * Because reserve locks the car around check + insert, exactly one CONFIRMED booking is created.
     */
    @Test
    void concurrentReserveNeverDoubleBooksSameCarForOverlappingDates() throws InterruptedException {
        int threads = 50;
        RentalCompany company = new RentalCompany(
                List.of(new Car("S1", "KA-02-2222", CarType.SUV, "BLR")),
                new DailyRatePricingStrategy());

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> reservationIds = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    Reservation r = company.reserve("S1", SEP_1, SEP_4);
                    reservationIds.add(r.getId());
                    successes.incrementAndGet();
                } catch (CarUnavailableException ignored) {
                    // Expected for the losing threads.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successes.get(), "only one overlapping reservation can win");
        assertEquals(1, reservationIds.stream().distinct().count());
        long blocking = company.reservationsForCar("S1").stream()
                .filter(Reservation::blocksAvailability)
                .count();
        assertEquals(1, blocking, "no overlapping confirmed/picked-up reservations exist");
    }
}
