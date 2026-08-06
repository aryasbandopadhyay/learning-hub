package com.example.scheduler.scheduler;

import com.example.scheduler.exception.InvalidScheduleException;
import com.example.scheduler.model.Job;
import com.example.scheduler.model.ScheduledTask;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Deterministic, thread-safe in-memory scheduler.
 *
 * <p><b>Core idea:</b> a min-heap ({@link PriorityQueue}) always keeps the earliest next-run task at
 * the top, so {@link #tick(Instant)} repeatedly peeks/polls while the top is due. That gives
 * O(log n) insertions and removals, which is the standard design for a timer/job scheduler.
 *
 * <p><b>Determinism:</b> tests call {@code tick(now)} directly and inject a mutable {@link Clock}.
 * There is no {@code Thread.sleep} in the engine, so tests are fast and non-flaky. A real product
 * could put a thin background worker on top that periodically calls tick(clock.instant()).
 *
 * <p><b>Concurrency:</b> {@code PriorityQueue} is not thread-safe, so every heap access is protected
 * by {@code queueLock}. We never execute user jobs while holding that lock; submissions from other
 * threads can continue, and a long-running job cannot block scheduling operations.
 */
public class JobScheduler {

    private final Clock clock;
    private final PriorityQueue<ScheduledTask> queue = new PriorityQueue<>();
    private final ReentrantLock queueLock = new ReentrantLock();
    private final AtomicLong sequence = new AtomicLong();

    private final Set<String> cancelledJobIds = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<String, AtomicInteger> runCounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Instant> lastRunAt = new ConcurrentHashMap<>();

    public JobScheduler(Clock clock) {
        this.clock = clock;
    }

    /** Schedule a one-shot job for an absolute instant. */
    public ScheduledTask schedule(Job job, Instant runAt) {
        cancelledJobIds.remove(job.getId());
        runCounts.putIfAbsent(job.getId(), new AtomicInteger());
        ScheduledTask task = new ScheduledTask(job, runAt, null, sequence.getAndIncrement());
        enqueue(task);
        return task;
    }

    /** Schedule a one-shot job relative to the injected clock. */
    public ScheduledTask scheduleAfter(Job job, Duration delay) {
        if (delay.isNegative()) {
            throw new InvalidScheduleException("Delay cannot be negative");
        }
        return schedule(job, clock.instant().plus(delay));
    }

    /** Schedule a recurring job; its first run is one interval from the injected clock's now. */
    public ScheduledTask scheduleRecurring(Job job, Duration interval) {
        validatePositive(interval, "Recurring interval");
        cancelledJobIds.remove(job.getId());
        runCounts.putIfAbsent(job.getId(), new AtomicInteger());
        ScheduledTask task = new ScheduledTask(
                job,
                clock.instant().plus(interval),
                interval,
                sequence.getAndIncrement());
        enqueue(task);
        return task;
    }

    /**
     * Cancel by job id. Existing heap entries are lazily skipped when they reach the top.
     *
     * <p>Lazy deletion keeps cancel O(1). Removing from the middle of a heap would be O(n), and for
     * machine-coding scope the small amount of stale heap data is a good trade-off.
     */
    public boolean cancel(String jobId) {
        return cancelledJobIds.add(jobId);
    }

    /** Run every task with nextRunAt <= now in heap order. Returns the number of executions. */
    public int tick(Instant now) {
        int executed = 0;
        while (true) {
            Optional<ScheduledTask> due = pollDue(now);
            if (due.isEmpty()) {
                return executed;
            }

            ScheduledTask task = due.get();
            String jobId = task.getJob().getId();
            if (cancelledJobIds.contains(jobId)) {
                continue;
            }

            task.getJob().run();
            runCounts.computeIfAbsent(jobId, ignored -> new AtomicInteger()).incrementAndGet();
            lastRunAt.put(jobId, now);
            executed++;

            if (task.isRecurring() && !cancelledJobIds.contains(jobId)) {
                Duration interval = task.getRecurringInterval().orElseThrow();
                enqueue(new ScheduledTask(
                        task.getJob(),
                        now.plus(interval),
                        interval,
                        sequence.getAndIncrement()));
            }
        }
    }

    public int getRunCount(String jobId) {
        AtomicInteger count = runCounts.get(jobId);
        return count == null ? 0 : count.get();
    }

    public Optional<Instant> getLastRunAt(String jobId) {
        return Optional.ofNullable(lastRunAt.get(jobId));
    }

    /** Snapshot of not-cancelled heap entries, useful for demo/tests. */
    public int pendingCount() {
        queueLock.lock();
        try {
            Set<String> liveEntries = new HashSet<>();
            for (ScheduledTask task : queue) {
                if (!cancelledJobIds.contains(task.getJob().getId())) {
                    liveEntries.add(task.getJob().getId() + "@" + task.getNextRunAt());
                }
            }
            return liveEntries.size();
        } finally {
            queueLock.unlock();
        }
    }

    private void enqueue(ScheduledTask task) {
        queueLock.lock();
        try {
            queue.add(task);
        } finally {
            queueLock.unlock();
        }
    }

    private Optional<ScheduledTask> pollDue(Instant now) {
        queueLock.lock();
        try {
            while (!queue.isEmpty()) {
                ScheduledTask task = queue.peek();
                if (task.getNextRunAt().isAfter(now)) {
                    return Optional.empty();
                }
                queue.poll();
                if (!cancelledJobIds.contains(task.getJob().getId())) {
                    return Optional.of(task);
                }
            }
            return Optional.empty();
        } finally {
            queueLock.unlock();
        }
    }

    private static void validatePositive(Duration value, String name) {
        if (value.isZero() || value.isNegative()) {
            throw new InvalidScheduleException(name + " must be positive");
        }
    }
}
