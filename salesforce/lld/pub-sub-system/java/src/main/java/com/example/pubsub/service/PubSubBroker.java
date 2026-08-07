package com.example.pubsub.service;

import com.example.pubsub.model.Message;
import com.example.pubsub.model.Topic;

import java.time.Clock;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application service / facade for the in-memory pub-sub system.
 *
 * <p><b>Observer pattern:</b> subscribers register callbacks for a topic. Publishers only know the
 * topic name and payload; the broker notifies all current observers.
 *
 * <p><b>Concurrency:</b> topics and subscription maps are {@link ConcurrentHashMap}s. Publishing
 * appends a message with a topic-owned atomic offset, then enqueues that same immutable message into
 * each active subscription's bounded queue. Each subscriber has its own dispatch thread and offset.
 */
public class PubSubBroker implements AutoCloseable {

    private static final int DEFAULT_QUEUE_CAPACITY = 1024;

    private final Clock clock;
    private final int queueCapacity;
    private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Subscription>> subscriptions =
            new ConcurrentHashMap<>();

    public PubSubBroker() {
        this(Clock.systemUTC(), DEFAULT_QUEUE_CAPACITY);
    }

    public PubSubBroker(Clock clock) {
        this(clock, DEFAULT_QUEUE_CAPACITY);
    }

    public PubSubBroker(Clock clock, int queueCapacity) {
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("queueCapacity must be positive");
        }
        this.clock = Objects.requireNonNull(clock, "clock");
        this.queueCapacity = queueCapacity;
    }

    /** Create or return an existing topic. Idempotent, which keeps demos/tests simple. */
    public Topic createTopic(String name) {
        return topics.computeIfAbsent(name, Topic::new);
    }

    /** Register/replace a subscriber for a topic. New subscribers receive future messages only. */
    public void subscribe(String topicName, String subscriberId, Subscriber subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        createTopic(topicName);
        var bySubscriber = subscriptions.computeIfAbsent(topicName, ignored -> new ConcurrentHashMap<>());
        Subscription replacement = new Subscription(topicName, subscriberId, subscriber, queueCapacity);
        Subscription old = bySubscriber.put(subscriberId, replacement);
        if (old != null) {
            old.stop();
        }
    }

    /** Remove a subscriber and stop its background dispatcher. Future publishes will not enqueue to it. */
    public void unsubscribe(String topicName, String subscriberId) {
        Map<String, Subscription> bySubscriber = subscriptions.get(topicName);
        if (bySubscriber == null) {
            return;
        }
        Subscription removed = bySubscriber.remove(subscriberId);
        if (removed != null) {
            removed.stop();
        }
    }

    /** Publish one payload to a topic and enqueue it for every active subscriber. */
    public Message publish(String topicName, String payload) {
        Topic topic = createTopic(topicName);
        Message message = topic.append(payload, clock);
        Map<String, Subscription> bySubscriber = subscriptions.get(topicName);
        if (bySubscriber != null) {
            bySubscriber.values().forEach(subscription -> subscription.enqueue(message));
        }
        return message;
    }

    /** Test/introspection helper: where the subscriber will read next after delivered callbacks. */
    public long nextOffset(String topicName, String subscriberId) {
        Map<String, Subscription> bySubscriber = subscriptions.get(topicName);
        if (bySubscriber == null || bySubscriber.get(subscriberId) == null) {
            return -1;
        }
        return bySubscriber.get(subscriberId).getNextOffset();
    }

    @Override
    public void close() {
        shutdown();
    }

    /** Stop every dispatcher thread; important for tests so the JVM can exit cleanly. */
    public void shutdown() {
        subscriptions.values().forEach(bySubscriber -> bySubscriber.values().forEach(Subscription::stop));
        subscriptions.clear();
    }
}
