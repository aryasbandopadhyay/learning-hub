package com.example.pool;

import com.example.pool.exception.InvalidResourceException;
import com.example.pool.exception.PoolTimeoutException;
import com.example.pool.model.Connection;
import com.example.pool.service.ConnectionPool;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionPoolTest {

    @Test
    void borrowUpToCapacityThenNextBorrowTimesOut() {
        ConnectionPool pool = new ConnectionPool(2);

        Connection first = pool.borrow(Duration.ofMillis(50));
        Connection second = pool.borrow(Duration.ofMillis(50));

        assertEquals(0, pool.available());
        assertThrows(PoolTimeoutException.class, () -> pool.borrow(Duration.ofMillis(25)));

        pool.release(first);
        pool.release(second);
    }

    @Test
    void releaseMakesConnectionAvailableAgain() {
        ConnectionPool pool = new ConnectionPool(1);
        Connection only = pool.borrow(Duration.ofMillis(50));
        assertEquals(0, pool.available());

        pool.release(only);

        assertEquals(1, pool.available());
        Connection borrowedAgain = pool.borrow(Duration.ofMillis(50));
        assertEquals(only.getId(), borrowedAgain.getId());
        pool.release(borrowedAgain);
    }

    @Test
    void doubleReleaseIsRejected() {
        ConnectionPool pool = new ConnectionPool(1);
        Connection only = pool.borrow(Duration.ofMillis(50));

        pool.release(only);

        assertThrows(InvalidResourceException.class, () -> pool.release(only));
        assertEquals(1, pool.available());
    }

    @Test
    void foreignReleaseIsRejected() {
        ConnectionPool pool = new ConnectionPool(1);
        assertThrows(InvalidResourceException.class, () -> pool.release(new Connection("foreign")));
        assertEquals(1, pool.available());
    }

    /**
     * More threads than capacity borrow and release. All eventually complete because each worker
     * waits long enough for a turn, and the active-id set proves no connection is handed to two
     * callers at the same time.
     */
    @Test
    void concurrentBorrowReleaseNeverOverAllocatesOrDoubleHandsOut() throws InterruptedException {
        int capacity = 5;
        int threads = 50;
        AtomicInteger created = new AtomicInteger();
        ConnectionPool pool = ConnectionPool.withCountingFactory(capacity, created);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        Set<String> activeIds = ConcurrentHashMap.newKeySet();
        ConcurrentLinkedQueue<String> borrowedIds = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();
        AtomicInteger maxActive = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    Connection connection = pool.borrow(Duration.ofSeconds(5));
                    if (!activeIds.add(connection.getId())) {
                        errors.add(new AssertionError("duplicate active connection " + connection.getId()));
                    }
                    borrowedIds.add(connection.getId());
                    maxActive.accumulateAndGet(activeIds.size(), Math::max);
                    Thread.sleep(10);
                    activeIds.remove(connection.getId());
                    pool.release(connection);
                } catch (Throwable t) {
                    errors.add(t);
                    Thread.currentThread().interrupt();
                }
            });
        }

        start.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertTrue(errors.isEmpty(), () -> "unexpected worker errors: " + errors);
        assertEquals(threads, borrowedIds.size(), "every thread eventually borrows once");
        assertEquals(capacity, created.get(), "factory called exactly capacity times");
        assertTrue(maxActive.get() <= capacity, "active resources never exceed capacity");
        assertEquals(capacity, pool.available());
        assertEquals(0, pool.borrowed());
    }
}
