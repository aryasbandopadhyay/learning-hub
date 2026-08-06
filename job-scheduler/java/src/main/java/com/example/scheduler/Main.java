package com.example.scheduler;

import com.example.scheduler.model.Job;
import com.example.scheduler.scheduler.JobScheduler;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Runnable demo. It uses deterministic tick calls so the printed output is stable in interviews,
 * CI, and local runs. A production shell could add a worker thread that calls tick(clock.instant()).
 */
public class Main {

    public static void main(String[] args) {
        Instant start = Instant.parse("2024-01-01T10:00:00Z");
        JobScheduler scheduler = new JobScheduler(Clock.fixed(start, ZoneOffset.UTC));
        List<String> auditLog = new ArrayList<>();

        Job email = new Job("email-digest", () -> auditLog.add("email-digest"));
        Job cleanup = new Job("cache-cleanup", () -> auditLog.add("cache-cleanup"));
        Job heartbeat = new Job("heartbeat", () -> auditLog.add("heartbeat"));

        scheduler.scheduleAfter(email, Duration.ofMinutes(5));
        scheduler.schedule(cleanup, start.plus(Duration.ofMinutes(2)));
        scheduler.scheduleRecurring(heartbeat, Duration.ofMinutes(3));

        System.out.println("Pending jobs at start: " + scheduler.pendingCount());
        System.out.println("Tick +2m ran: " + scheduler.tick(start.plus(Duration.ofMinutes(2))));
        System.out.println("Tick +3m ran: " + scheduler.tick(start.plus(Duration.ofMinutes(3))));
        System.out.println("Tick +5m ran: " + scheduler.tick(start.plus(Duration.ofMinutes(5))));
        System.out.println("Run order: " + auditLog);
        System.out.println("Heartbeat runs: " + scheduler.getRunCount("heartbeat"));
    }
}
