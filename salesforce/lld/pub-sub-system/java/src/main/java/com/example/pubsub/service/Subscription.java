package com.example.pubsub.service;

import com.example.pubsub.model.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Runtime state for one subscriber attached to one topic.
 *
 * <p>Each subscription owns a bounded queue and exactly one worker thread. That gives FIFO delivery
 * for this subscriber, isolates slow subscribers from each other, and creates simple backpressure:
 * when this queue is full, publishers block while enqueuing to this subscriber instead of dropping
 * messages. The worker advances {@code nextOffset} only after the callback returns successfully.
 */
class Subscription {

    private final String subscriberId;
    private final Subscriber subscriber;
    private final BlockingQueue<Message> queue;
    private final AtomicLong nextOffset = new AtomicLong();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Thread worker;

    Subscription(String topicName, String subscriberId, Subscriber subscriber, int queueCapacity) {
        this.subscriberId = subscriberId;
        this.subscriber = subscriber;
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.worker = new Thread(this::dispatchLoop, "pubsub-" + topicName + "-" + subscriberId);
        this.worker.setDaemon(true);
        this.worker.start();
    }

    /** Enqueue one message, waiting if this subscriber's bounded queue is currently full. */
    void enqueue(Message message) {
        if (!running.get()) {
            return;
        }
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while enqueueing message", e);
        }
    }

    /** Stop future delivery and wake the worker if it is blocked in take/poll. */
    void stop() {
        running.set(false);
        worker.interrupt();
    }

    long getNextOffset() {
        return nextOffset.get();
    }

    String getSubscriberId() {
        return subscriberId;
    }

    private void dispatchLoop() {
        while (running.get() || !queue.isEmpty()) {
            try {
                Message message = queue.poll(100, TimeUnit.MILLISECONDS);
                if (message == null) {
                    continue;
                }
                if (!running.get()) {
                    continue;
                }
                subscriber.onMessage(message);
                nextOffset.updateAndGet(current -> Math.max(current, message.getOffset() + 1));
            } catch (InterruptedException e) {
                if (!running.get()) {
                    return;
                }
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
