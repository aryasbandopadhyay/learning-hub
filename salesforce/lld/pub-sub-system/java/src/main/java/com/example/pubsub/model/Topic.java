package com.example.pubsub.model;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A named append-only stream of messages.
 *
 * <p>The {@link AtomicLong} is the topic's offset allocator. Even if 100 publisher threads append
 * concurrently, each gets a unique increasing offset. The history list is synchronized only for the
 * short add/snapshot operations; subscriber delivery happens outside this class.
 */
public class Topic {

    private final String name;
    private final AtomicLong nextOffset = new AtomicLong();
    private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());

    public Topic(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("topic name must not be blank");
        }
        this.name = name;
    }

    /** Append one payload and return the immutable Message assigned by this topic. */
    public Message append(String payload, Clock clock) {
        Objects.requireNonNull(clock, "clock");
        long offset = nextOffset.getAndIncrement();
        Message message = new Message(name, offset, payload, clock.instant());
        messages.add(message);
        return message;
    }

    /** Snapshot for tests/introspection; callers cannot mutate the internal history. */
    public List<Message> snapshot() {
        synchronized (messages) {
            return List.copyOf(messages);
        }
    }

    public String getName() {
        return name;
    }
}
