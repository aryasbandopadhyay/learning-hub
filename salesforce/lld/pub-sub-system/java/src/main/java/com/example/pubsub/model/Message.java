package com.example.pubsub.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable event flowing through the broker.
 *
 * <p>The broker owns the id and offset. Subscribers receive the same Message instance, but because
 * all fields are final there is no shared mutable state for one subscriber to accidentally corrupt
 * for another. This is especially important in a concurrent in-memory system.
 */
public final class Message {

    private final String id;
    private final String topicName;
    private final long offset;
    private final String payload;
    private final Instant publishedAt;

    public Message(String topicName, long offset, String payload, Instant publishedAt) {
        this.id = UUID.randomUUID().toString();
        this.topicName = Objects.requireNonNull(topicName, "topicName");
        this.offset = offset;
        this.payload = Objects.requireNonNull(payload, "payload");
        this.publishedAt = Objects.requireNonNull(publishedAt, "publishedAt");
    }

    public String getId() {
        return id;
    }

    public String getTopicName() {
        return topicName;
    }

    public long getOffset() {
        return offset;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
