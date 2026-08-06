package com.example.notification.channel;

import com.example.notification.model.ChannelType;

/**
 * Strategy pattern: every channel has the same send contract, while Email/SMS/Push hide their own
 * provider details. The service depends on this interface, not concrete implementations.
 */
public interface NotificationChannel {

    ChannelType type();

    void send(String recipient, String message);
}
