package com.example.pool.model;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fake in-memory connection used for the LLD question.
 *
 * <p>It deliberately has no DB/network behavior. The pool only needs a reusable object with an id
 * and open/close/isValid lifecycle methods, so tests stay deterministic and offline.
 */
public class Connection {

    private final String id;
    private final AtomicBoolean open = new AtomicBoolean(true);

    public Connection(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isOpen() {
        return open.get();
    }

    /** A closed connection is invalid and should not be handed to clients. */
    public boolean isValid() {
        return open.get();
    }

    /** Close is idempotent, matching how many real connection-like resources behave. */
    public void close() {
        open.set(false);
    }

    @Override
    public String toString() {
        return "Connection{" + id + "}";
    }
}
