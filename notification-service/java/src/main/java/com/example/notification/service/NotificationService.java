package com.example.notification.service;

import com.example.notification.channel.ChannelFactory;
import com.example.notification.channel.NotificationChannel;
import com.example.notification.model.ChannelType;
import com.example.notification.model.NotificationEvent;
import com.example.notification.model.NotificationResult;
import com.example.notification.model.User;

import java.time.Clock;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Application service that wires Factory + Strategy + Observer together.
 *
 * <p>Concurrency: the service itself is stateless per call. Registered observers live in a
 * {@link CopyOnWriteArrayList}, so multiple sender threads can iterate safely while another thread
 * registers a listener. Channel sinks and audit listeners are also thread-safe.
 */
public class NotificationService {

    private final ChannelFactory channelFactory;
    private final int maxAttempts;
    private final Clock clock;
    private final CopyOnWriteArrayList<NotificationListener> listeners = new CopyOnWriteArrayList<>();

    public NotificationService(ChannelFactory channelFactory, int maxAttempts, Clock clock) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }
        this.channelFactory = channelFactory;
        this.maxAttempts = maxAttempts;
        this.clock = clock;
    }

    /** Use the user's preferred channels when the caller does not specify channels explicitly. */
    public Map<ChannelType, NotificationResult> notify(User user, String message) {
        return notify(user, message, user.getPreferredChannels());
    }

    /** Send to every requested channel and return one result per channel; never throws on send failure. */
    public Map<ChannelType, NotificationResult> notify(User user,
                                                       String message,
                                                       Collection<ChannelType> channelTypes) {
        Map<ChannelType, NotificationResult> results = new EnumMap<>(ChannelType.class);
        for (ChannelType type : channelTypes) {
            NotificationResult result = sendWithRetry(user, message, type);
            results.put(type, result);
            publish(new NotificationEvent(
                    user,
                    type,
                    result.status(),
                    message,
                    result.attempts(),
                    result.errorMessage(),
                    clock.instant()));
        }
        return results;
    }

    public void registerListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    private NotificationResult sendWithRetry(User user, String message, ChannelType type) {
        NotificationChannel channel = channelFactory.create(type);
        String recipient = user.recipientFor(type);
        String lastError = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                channel.send(recipient, message);
                return NotificationResult.sent(type, attempt);
            } catch (RuntimeException ex) {
                lastError = ex.getMessage();
            }
        }
        return NotificationResult.failed(type, maxAttempts, lastError);
    }

    private void publish(NotificationEvent event) {
        for (NotificationListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
