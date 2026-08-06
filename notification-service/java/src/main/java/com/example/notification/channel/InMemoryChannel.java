package com.example.notification.channel;

import com.example.notification.model.ChannelType;
import com.example.notification.model.SentMessage;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base fake provider for machine coding: records deliveries in memory instead of calling networks.
 *
 * <p>Concurrency: {@link CopyOnWriteArrayList} makes appends safe when many threads send at once
 * and gives tests/listeners a stable snapshot without external locking.
 */
public abstract class InMemoryChannel implements NotificationChannel {

    private final ChannelType type;
    private final Clock clock;
    private final CopyOnWriteArrayList<SentMessage> sentMessages = new CopyOnWriteArrayList<>();

    protected InMemoryChannel(ChannelType type, Clock clock) {
        this.type = type;
        this.clock = clock;
    }

    @Override
    public ChannelType type() {
        return type;
    }

    @Override
    public void send(String recipient, String message) {
        sentMessages.add(new SentMessage(type, recipient, message, clock.instant()));
    }

    public List<SentMessage> getSentMessages() {
        return List.copyOf(sentMessages);
    }
}
