package com.example.scheduler.model;

import java.util.Objects;

/**
 * Command object: a Job packages "what to run" with a stable id the scheduler can track/cancel.
 *
 * <p>The scheduler does not know whether this command sends email, cleans cache, or calls an API.
 * That separation is the Command pattern and keeps scheduling independent from business logic.
 */
public class Job {

    private final String id;
    private final Runnable command;

    public Job(String id, Runnable command) {
        this.id = Objects.requireNonNull(id, "id");
        this.command = Objects.requireNonNull(command, "command");
    }

    public String getId() {
        return id;
    }

    public void run() {
        command.run();
    }

    @Override
    public String toString() {
        return "Job{" + id + '}';
    }
}
