package com.example.locker;

import com.example.locker.exception.InvalidPickupCodeException;
import com.example.locker.exception.NoAvailableLockerException;
import com.example.locker.factory.LockerFactory;
import com.example.locker.model.DeliveryPackage;
import com.example.locker.model.LockerSize;
import com.example.locker.model.PackageSize;
import com.example.locker.service.AmazonLockerService;
import com.example.locker.service.LockerLocation;
import com.example.locker.strategy.SmallestFitAssignmentStrategy;
import org.junit.jupiter.api.Test;

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

class AmazonLockerTest {

    private AmazonLockerService newService(int small, int medium, int large) {
        return new AmazonLockerService(
                LockerLocation.of("LOC1", small, medium, large),
                new SmallestFitAssignmentStrategy());
    }

    @Test
    void smallestFitUsesSmallThenMediumWhenSmallIsFull() {
        AmazonLockerService service = newService(1, 1, 0);

        String first = service.deliver(LockerFactory.createPackage("S1", PackageSize.SMALL));
        assertEquals(LockerSize.SMALL, service.findLocker(first).orElseThrow().getSize());

        String second = service.deliver(LockerFactory.createPackage("S2", PackageSize.SMALL));
        assertEquals(LockerSize.MEDIUM, service.findLocker(second).orElseThrow().getSize());
    }

    @Test
    void largePackageCannotFitSmallLocker() {
        AmazonLockerService service = newService(1, 0, 0);
        assertThrows(NoAvailableLockerException.class,
                () -> service.deliver(LockerFactory.createPackage("L1", PackageSize.LARGE)));
    }

    @Test
    void deliverThenPickupReturnsSamePackageAndFreesLocker() {
        AmazonLockerService service = newService(1, 0, 0);
        DeliveryPackage pkg = LockerFactory.createPackage("S1", PackageSize.SMALL);

        String code = service.deliver(pkg);
        assertNotNull(code);
        assertEquals(0, service.availableLockers());

        DeliveryPackage picked = service.pickup(code);
        assertEquals(pkg, picked);
        assertEquals(1, service.availableLockers());
    }

    @Test
    void invalidPickupCodeIsRejected() {
        AmazonLockerService service = newService(1, 0, 0);
        assertThrows(InvalidPickupCodeException.class, () -> service.pickup("000000"));
    }

    @Test
    void alreadyUsedPickupCodeIsRejected() {
        AmazonLockerService service = newService(1, 0, 0);
        String code = service.deliver(LockerFactory.createPackage("S1", PackageSize.SMALL));
        service.pickup(code);
        assertThrows(InvalidPickupCodeException.class, () -> service.pickup(code));
    }

    @Test
    void fullLocationThrowsNoAvailableLocker() {
        AmazonLockerService service = newService(1, 0, 0);
        service.deliver(LockerFactory.createPackage("S1", PackageSize.SMALL));
        assertThrows(NoAvailableLockerException.class,
                () -> service.deliver(LockerFactory.createPackage("S2", PackageSize.SMALL)));
    }

    /** 50 threads race for 5 medium lockers; exactly 5 win and no locker is double-allocated. */
    @Test
    void concurrentDeliveryNeverDoubleAllocates() throws InterruptedException {
        int mediumLockers = 5;
        int threads = 50;
        AmazonLockerService service = newService(0, mediumLockers, 0);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> claimedLockerIds = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await(); // release all threads at once to maximize contention
                    String code = service.deliver(LockerFactory.createPackage("M" + id, PackageSize.MEDIUM));
                    successes.incrementAndGet();
                    claimedLockerIds.add(service.findLocker(code).orElseThrow().getId());
                } catch (NoAvailableLockerException ignored) {
                    // expected for the losers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(mediumLockers, successes.get(), "exactly the capacity should deliver");
        assertEquals(mediumLockers, claimedLockerIds.stream().distinct().count(),
                "no locker id may be claimed twice");
        assertEquals(0, service.availableLockers());
    }
}
