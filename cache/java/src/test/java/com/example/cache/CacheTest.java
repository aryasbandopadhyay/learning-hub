package com.example.cache;

import com.example.cache.eviction.LfuEvictionPolicy;
import com.example.cache.eviction.LruEvictionPolicy;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheTest {

    @Test
    void lruEvictsLeastRecentlyUsedKey() {
        Cache<String, Integer> cache = new Cache<>(2, new LruEvictionPolicy<>());
        cache.put("a", 1);
        cache.put("b", 2);
        assertEquals(Optional.of(1), cache.get("a"));

        cache.put("c", 3);

        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
        assertTrue(cache.containsKey("c"));
    }

    @Test
    void lruUpdateRefreshesRecency() {
        Cache<String, Integer> cache = new Cache<>(2, new LruEvictionPolicy<>());
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("a", 10);

        cache.put("c", 3);

        assertEquals(Optional.of(10), cache.get("a"));
        assertFalse(cache.containsKey("b"));
        assertTrue(cache.containsKey("c"));
    }

    @Test
    void lfuEvictsLowestFrequencyKey() {
        Cache<String, Integer> cache = new Cache<>(2, new LfuEvictionPolicy<>());
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");
        cache.get("a");
        cache.get("b");

        cache.put("c", 3);

        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
        assertTrue(cache.containsKey("c"));
    }

    @Test
    void lfuBreaksFrequencyTiesByLeastRecentUse() {
        Cache<String, Integer> cache = new Cache<>(2, new LfuEvictionPolicy<>());
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a"); // both become freq 2 after the next line; a is older within that bucket
        cache.get("b");

        cache.put("c", 3);

        assertFalse(cache.containsKey("a"));
        assertTrue(cache.containsKey("b"));
        assertTrue(cache.containsKey("c"));
    }

    @Test
    void missingGetReturnsEmptyAndSizeNeverExceedsCapacity() {
        Cache<String, Integer> cache = new Cache<>(2, new LruEvictionPolicy<>());
        assertTrue(cache.get("missing").isEmpty());
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        assertEquals(2, cache.size());
    }

    @Test
    void concurrentHammerNeverExceedsCapacityOrCrashes() throws InterruptedException {
        int capacity = 5;
        int threads = 32;
        int operations = 1_000;
        Cache<Integer, Integer> cache = new Cache<>(capacity, new LruEvictionPolicy<>());
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger errors = new AtomicInteger();

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < operations; i++) {
                        int key = (threadId + i) % 20;
                        cache.put(key, i);
                        cache.get((key + 1) % 20);
                        if (cache.size() > capacity) {
                            errors.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        assertEquals(0, errors.get());
        assertTrue(cache.size() <= capacity);
    }

    @Test
    void concurrentCapacityOnePutsLeaveExactlyOneEntry() throws InterruptedException {
        Cache<Integer, Integer> cache = new Cache<>(1, new LfuEvictionPolicy<>());
        int threads = 40;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Exception> error = new AtomicReference<>();

        for (int i = 0; i < threads; i++) {
            final int key = i;
            pool.submit(() -> {
                try {
                    start.await();
                    cache.put(key, key);
                } catch (Exception e) {
                    error.compareAndSet(null, e);
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        assertEquals(null, error.get());
        assertEquals(1, cache.size());
    }
}
