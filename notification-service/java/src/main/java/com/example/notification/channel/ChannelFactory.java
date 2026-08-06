package com.example.notification.channel;

import com.example.notification.model.ChannelType;

import java.time.Clock;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory pattern: resolves enum input to a channel strategy.
 *
 * <p>The default factory owns singleton in-memory channels, so tests and demos can inspect their
 * sinks. Tests may inject a failing channel by calling {@link #register(NotificationChannel)}.
 */
public class ChannelFactory {

    private final Map<ChannelType, NotificationChannel> channels = new ConcurrentHashMap<>();

    public ChannelFactory(Clock clock) {
        register(new EmailChannel(clock));
        register(new SmsChannel(clock));
        register(new PushChannel(clock));
    }

    public ChannelFactory(Map<ChannelType, NotificationChannel> channels) {
        channels.values().forEach(this::register);
    }

    public NotificationChannel create(ChannelType type) {
        NotificationChannel channel = channels.get(type);
        if (channel == null) {
            throw new IllegalArgumentException("Unsupported channel type: " + type);
        }
        return channel;
    }

    public void register(NotificationChannel channel) {
        channels.put(channel.type(), channel);
    }

    public Map<ChannelType, NotificationChannel> snapshot() {
        return new EnumMap<>(channels);
    }
}
