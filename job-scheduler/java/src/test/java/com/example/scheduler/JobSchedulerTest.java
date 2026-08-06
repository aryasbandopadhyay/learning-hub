package com.example.scheduler;

import com.example.scheduler.model.Job;
import com.example.scheduler.scheduler.JobScheduler;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A mutable clock makes scheduler tests deterministic: no real threads or sleeps are needed. */
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

class JobSchedulerTest {

    private final Instant start = Instant.parse("2024-01-01T10:00:00Z");

    @Test
    void oneShotRunsOnlyWhenTickReachesRunAt() {
        MutableClock clock = new MutableClock(start);
        JobScheduler scheduler = new JobScheduler(clock);
        AtomicInteger runs = new AtomicInteger();
        Job job = new Job("report", runs::incrementAndGet);

        scheduler.schedule(job, start.plus(Duration.ofMinutes(10)));

        assertEquals(0, scheduler.tick(start.plus(Duration.ofMinutes(9))));
        assertEquals(0, runs.get());
        clock.advance(Duration.ofMinutes(10));
        assertEquals(1, scheduler.tick(clock.instant()));
        assertEquals(1, runs.get());
        assertEquals(1, scheduler.getRunCount("report"));
    }

    @Test
    void jobsRunInDueTimeOrderWithinSingleTick() {
        JobScheduler scheduler = new JobScheduler(Clock.fixed(start, ZoneOffset.UTC));
        List<String> order = new ArrayList<>();

        scheduler.schedule(new Job("second", () -> order.add("second")), start.plusSeconds(20));
        scheduler.schedule(new Job("first", () -> order.add("first")), start.plusSeconds(10));

        assertEquals(2, scheduler.tick(start.plusSeconds(30)));
        assertEquals(List.of("first", "second"), order);
    }

    @Test
    void recurringJobReschedulesAfterEachDueTick() {
        MutableClock clock = new MutableClock(start);
        JobScheduler scheduler = new JobScheduler(clock);
        Job heartbeat = new Job("heartbeat", () -> { });

        scheduler.scheduleRecurring(heartbeat, Duration.ofMinutes(5));

        clock.advance(Duration.ofMinutes(5));
        assertEquals(1, scheduler.tick(clock.instant()));
        clock.advance(Duration.ofMinutes(5));
        assertEquals(1, scheduler.tick(clock.instant()));
        clock.advance(Duration.ofMinutes(5));
        assertEquals(1, scheduler.tick(clock.instant()));

        assertEquals(3, scheduler.getRunCount("heartbeat"));
        assertEquals(clock.instant(), scheduler.getLastRunAt("heartbeat").orElseThrow());
    }

    @Test
    void cancelledJobDoesNotRun() {
        JobScheduler scheduler = new JobScheduler(Clock.fixed(start, ZoneOffset.UTC));
        AtomicInteger runs = new AtomicInteger();
        scheduler.schedule(new Job("obsolete", runs::incrementAndGet), start.plusSeconds(1));

        scheduler.cancel("obsolete");

        assertEquals(0, scheduler.tick(start.plusSeconds(10)));
        assertEquals(0, runs.get());
        assertEquals(0, scheduler.getRunCount("obsolete"));
    }

    /** Many producers schedule at once; the lock around the heap means no submissions are lost. */
    @Test
    void concurrentSubmissionsAreAllEnqueuedAndRunOnce() throws InterruptedException {
        int threads = 50;
        JobScheduler scheduler = new JobScheduler(Clock.fixed(start, ZoneOffset.UTC));
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch gate = new CountDownLatch(1);
        AtomicInteger runCounter = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    gate.await(); // release all producers together to maximize queue contention
                    scheduler.schedule(
                            new Job("job-" + id, runCounter::incrementAndGet),
                            start.plusSeconds(1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        gate.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        assertEquals(threads, scheduler.pendingCount());

        assertEquals(threads, scheduler.tick(start.plusSeconds(60)));
        assertEquals(threads, runCounter.get());
    }
}
