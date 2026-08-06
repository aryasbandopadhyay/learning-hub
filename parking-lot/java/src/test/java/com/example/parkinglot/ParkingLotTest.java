package com.example.parkinglot;

import com.example.parkinglot.exception.InvalidTicketException;
import com.example.parkinglot.exception.NoAvailableSpotException;
import com.example.parkinglot.factory.VehicleFactory;
import com.example.parkinglot.model.SpotType;
import com.example.parkinglot.model.Ticket;
import com.example.parkinglot.model.Vehicle;
import com.example.parkinglot.model.VehicleType;
import com.example.parkinglot.service.Level;
import com.example.parkinglot.service.ParkingLot;
import com.example.parkinglot.service.Receipt;
import com.example.parkinglot.strategy.HourlyFeeStrategy;
import com.example.parkinglot.strategy.NearestFirstAssignmentStrategy;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A mutable clock we can advance by hand, so fee tests are 100% deterministic (no Thread.sleep).
 */
class MutableClock extends Clock {
    private Instant now;

    MutableClock(Instant start) {
        this.now = start;
    }

    void advance(Duration d) {
        now = now.plus(d);
    }

    @Override
    public Instant instant() {
        return now;
    }

    @Override
    public ZoneOffset getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(java.time.ZoneId zone) {
        return this;
    }
}

class ParkingLotTest {

    private ParkingLot newLot(Clock clock) {
        // One level: 1 small, 1 medium, 1 large.
        return new ParkingLot(
                List.of(Level.of(0, 1, 1, 1)),
                new NearestFirstAssignmentStrategy(),
                new HourlyFeeStrategy(),
                clock);
    }

    @Test
    void motorcycleFitsSmallSpotAndReducesAvailability() {
        ParkingLot lot = newLot(Clock.systemUTC());
        assertEquals(3, lot.availableSpots());

        Ticket t = lot.park(VehicleFactory.create(VehicleType.MOTORCYCLE, "M1"));
        assertNotNull(t.getId());
        assertEquals(SpotType.SMALL, t.getSpot().getSpotType());
        assertEquals(2, lot.availableSpots());
    }

    @Test
    void truckOnlyFitsLargeSpot() {
        ParkingLot lot = newLot(Clock.systemUTC());
        Ticket t = lot.park(VehicleFactory.create(VehicleType.TRUCK, "T1"));
        assertEquals(SpotType.LARGE, t.getSpot().getSpotType());
    }

    @Test
    void throwsWhenNoCompatibleSpotLeft() {
        // Lot with only a SMALL spot; a truck cannot ever fit.
        ParkingLot lot = new ParkingLot(
                List.of(Level.of(0, 1, 0, 0)),
                new NearestFirstAssignmentStrategy(),
                new HourlyFeeStrategy(),
                Clock.systemUTC());

        assertThrows(NoAvailableSpotException.class,
                () -> lot.park(VehicleFactory.create(VehicleType.TRUCK, "T1")));
    }

    @Test
    void unparkComputesFeeAndFreesSpot() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T10:00:00Z"));
        ParkingLot lot = newLot(clock);

        Ticket t = lot.park(VehicleFactory.create(VehicleType.CAR, "C1")); // MEDIUM rate = 20/hr
        clock.advance(Duration.ofMinutes(90)); // 1.5h -> rounds up to 2 hours

        Receipt r = lot.unpark(t.getId());
        assertEquals(2 * 20L, r.fee());
        assertEquals(3, lot.availableSpots()); // spot released
    }

    @Test
    void minimumOneHourCharged() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T10:00:00Z"));
        ParkingLot lot = newLot(clock);

        Ticket t = lot.park(VehicleFactory.create(VehicleType.MOTORCYCLE, "M1")); // SMALL = 10/hr
        clock.advance(Duration.ofMinutes(5)); // < 1 hour

        Receipt r = lot.unpark(t.getId());
        assertEquals(10L, r.fee());
    }

    @Test
    void unparkTwiceIsRejected() {
        ParkingLot lot = newLot(Clock.systemUTC());
        Ticket t = lot.park(VehicleFactory.create(VehicleType.CAR, "C1"));
        lot.unpark(t.getId());
        assertThrows(InvalidTicketException.class, () -> lot.unpark(t.getId()));
    }

    /**
     * Concurrency test: many threads race to park more cars than there are MEDIUM spots. Exactly the
     * available number must succeed, and no two winners may share a spot id.
     */
    @Test
    void concurrentParkingNeverDoubleAllocates() throws InterruptedException {
        int mediumSpots = 5;
        int threads = 50;
        ParkingLot lot = new ParkingLot(
                List.of(Level.of(0, 0, mediumSpots, 0)),
                new NearestFirstAssignmentStrategy(),
                new HourlyFeeStrategy(),
                Clock.systemUTC());

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> claimedSpotIds = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await(); // release all threads at once to maximize contention
                    Vehicle car = VehicleFactory.create(VehicleType.CAR, "C" + id);
                    Ticket t = lot.park(car);
                    successes.incrementAndGet();
                    claimedSpotIds.add(t.getSpot().getId());
                } catch (NoAvailableSpotException ignored) {
                    // expected for the losers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(mediumSpots, successes.get(), "exactly the capacity should park");
        assertEquals(mediumSpots, claimedSpotIds.stream().distinct().count(),
                "no spot id may be claimed twice");
        assertEquals(0, lot.availableSpots());
    }
}
