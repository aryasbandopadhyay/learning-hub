package com.example.scheduler.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * One heap entry for a job.
 *
 * <p>PriorityQueue needs a comparable value. We order by nextRunAt first, then by insertion sequence
 * (FIFO for equal times), then by job id as a final stable tie-breaker. recurringInterval is empty
 * for one-shot jobs and present for recurring jobs.
 */
public class ScheduledTask implements Comparable<ScheduledTask> {

    private final Job job;
    private final Instant nextRunAt;
    private final Duration recurringInterval;
    private final long sequence;

    public ScheduledTask(Job job, Instant nextRunAt, Duration recurringInterval, long sequence) {
        this.job = job;
        this.nextRunAt = nextRunAt;
        this.recurringInterval = recurringInterval;
        this.sequence = sequence;
    }

    public Job getJob() {
        return job;
    }

    public Instant getNextRunAt() {
        return nextRunAt;
    }

    public Optional<Duration> getRecurringInterval() {
        return Optional.ofNullable(recurringInterval);
    }

    public boolean isRecurring() {
        return recurringInterval != null;
    }

    @Override
    public int compareTo(ScheduledTask other) {
        int byTime = nextRunAt.compareTo(other.nextRunAt);
        if (byTime != 0) {
            return byTime;
        }
        int bySequence = Long.compare(sequence, other.sequence);
        if (bySequence != 0) {
            return bySequence;
        }
        return job.getId().compareTo(other.job.getId());
    }
}
