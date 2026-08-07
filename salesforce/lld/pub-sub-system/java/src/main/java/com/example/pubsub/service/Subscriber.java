package com.example.pubsub.service;

import com.example.pubsub.model.Message;

/**
 * Observer callback invoked by the broker when a message reaches a subscriber.
 *
 * <p>The interface is deliberately tiny and functional, so a lambda can be used in demos/tests:
 * {@code message -> received.add(message)}. In a real system this might be an adapter to email,
 * analytics, search indexing, or a WebSocket push service.
 */
@FunctionalInterface
public interface Subscriber {
    void onMessage(Message message);
}
