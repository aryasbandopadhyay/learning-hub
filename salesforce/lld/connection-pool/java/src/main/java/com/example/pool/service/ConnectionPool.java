package com.example.pool.service;

import com.example.pool.exception.InvalidResourceException;
import com.example.pool.exception.PoolTimeoutException;
import com.example.pool.factory.ConnectionFactory;
import com.example.pool.factory.InMemoryConnectionFactory;
import com.example.pool.model.Connection;
import com.example.pool.strategy.DefaultValidationStrategy;
import com.example.pool.strategy.ValidationStrategy;

import java.time.Duration;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bounded, thread-safe resource pool.
 *
 * <p>The MVP eagerly creates {@code maxSize} fake connections up front, so capacity is fixed and
 * the pool can never over-allocate. Free resources live in a bounded {@link LinkedBlockingQueue};
 * callers of {@link #borrow(Duration)} block on that queue until another thread releases a resource
 * or the timeout expires.
 *
 * <p>Two identity-based sets guard ownership: {@code allConnections} rejects foreign resources,
 * while {@code borrowedConnections} rejects double release. They are protected by one small lock so
 * check-and-mark operations are atomic without blocking the queue itself.
 */
public class ConnectionPool {

    private final int maxSize;
    private final LinkedBlockingQueue<Connection> availableConnections;
    private final Set<Connection> allConnections;
    private final Set<Connection> borrowedConnections;
    private final Object ownershipLock = new Object();
    private final ValidationStrategy validationStrategy;

    public ConnectionPool(int maxSize) {
        this(maxSize, new InMemoryConnectionFactory(), new DefaultValidationStrategy());
    }

    public ConnectionPool(int maxSize,
                          ConnectionFactory factory,
                          ValidationStrategy validationStrategy) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        this.maxSize = maxSize;
        this.availableConnections = new LinkedBlockingQueue<>(maxSize);
        this.allConnections = Collections.newSetFromMap(new IdentityHashMap<>());
        this.borrowedConnections = Collections.newSetFromMap(new IdentityHashMap<>());
        this.validationStrategy = validationStrategy;

        for (int i = 1; i <= maxSize; i++) {
            Connection connection = factory.create("conn-" + i);
            allConnections.add(connection);
            availableConnections.add(connection);
        }
    }

    /**
     * Borrow a resource, waiting up to {@code timeout} for one to become free.
     *
     * <p>{@code poll(timeout)} provides the blocking behavior. Once a connection is removed from the
     * queue, no other caller can receive it. We then mark it as borrowed under {@code ownershipLock}
     * and run the validation strategy before handing it to the client.
     */
    public Connection borrow(Duration timeout) {
        try {
            Connection connection = availableConnections.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new PoolTimeoutException("Timed out waiting for a connection");
            }
            synchronized (ownershipLock) {
                borrowedConnections.add(connection);
            }
            if (!validationStrategy.isValid(connection)) {
                release(connection);
                throw new InvalidResourceException("Borrowed connection is not valid: " + connection.getId());
            }
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PoolTimeoutException("Interrupted while waiting for a connection");
        }
    }

    /**
     * Return a borrowed resource to the pool.
     *
     * <p>The identity checks make ownership explicit: a newly-created/foreign connection is rejected,
     * and a connection already returned once is rejected as a double release.
     */
    public void release(Connection connection) {
        synchronized (ownershipLock) {
            if (!allConnections.contains(connection)) {
                throw new InvalidResourceException("Connection does not belong to this pool");
            }
            if (!borrowedConnections.remove(connection)) {
                throw new InvalidResourceException("Connection was not borrowed or was already released");
            }
        }
        availableConnections.offer(connection);
    }

    /** Configured capacity, not the current number of free resources. */
    public int size() {
        return maxSize;
    }

    /** Current free-resource count (a snapshot under concurrent load). */
    public int available() {
        return availableConnections.size();
    }

    public int borrowed() {
        synchronized (ownershipLock) {
            return borrowedConnections.size();
        }
    }

    public static ConnectionPool withCountingFactory(int maxSize, AtomicInteger createdCount) {
        return new ConnectionPool(maxSize, id -> {
            createdCount.incrementAndGet();
            return new Connection(id);
        }, new DefaultValidationStrategy());
    }
}
