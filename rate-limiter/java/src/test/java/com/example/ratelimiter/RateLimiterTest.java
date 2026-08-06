package com.example.ratelimiter;

import com.example.ratelimiter.algorithms.FixedWindowLimiter;
import com.example.ratelimiter.algorithms.TokenBucketLimiter;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A mutable clock we advance by hand, so time-based limiter tests never need Thread.sleep. */
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
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }
}

class RateLimiterTest {

    @Test
    void tokenBucketAllowsCapacityThenDeniesUntilRefill() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        RateLimiter limiter = new TokenBucketLimiter(3, 2.0, clock);

        assertTrue(limiter.allow("client-a"));
        assertTrue(limiter.allow("client-a"));
        assertTrue(limiter.allow("client-a"));
        assertFalse(limiter.allow("client-a"));

        clock.advance(Duration.ofSeconds(1)); // 2 tokens refilled at 2/sec
        assertTrue(limiter.allow("client-a"));
        assertTrue(limiter.allow("client-a"));
        assertFalse(limiter.allow("client-a"));
    }

    @Test
    void fixedWindowAllowsNThenResetsAfterWindow() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        RateLimiter limiter = new FixedWindowLimiter(2, Duration.ofSeconds(10), clock);

        assertTrue(limiter.allow("client-a"));
        assertTrue(limiter.allow("client-a"));
        assertFalse(limiter.allow("client-a"));

        clock.advance(Duration.ofSeconds(10));
        assertTrue(limiter.allow("client-a"));
        assertTrue(limiter.allow("client-a"));
        assertFalse(limiter.allow("client-a"));
    }

    @Test
    void tokenBucketStateIsIsolatedPerClient() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        RateLimiter limiter = new TokenBucketLimiter(1, 1.0, clock);

        assertTrue(limiter.allow("client-a"));
        assertFalse(limiter.allow("client-a"));
        assertTrue(limiter.allow("client-b"));
    }

    /**
     * Concurrency test: many threads hit one client in the same fixed window. Exactly N can pass.
     * If check+increment were not atomic, this test would occasionally over-admit.
     */
    @Test
    void concurrentFixedWindowAllowsExactlyCapacity() throws InterruptedException {
        int limit = 5;
        int threads = 50;
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        RateLimiter limiter = new FixedWindowLimiter(limit, Duration.ofMinutes(1), clock);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    if (limiter.allow("client-a")) {
                        successes.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(limit, successes.get(), "exactly the configured limit should pass");
    }
}
