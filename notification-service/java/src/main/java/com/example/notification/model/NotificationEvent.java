package com.example.notification.model;

import java.time.Instant;

/** Event delivered to observers after a channel reaches its final SENT/FAILED outcome. */
public record NotificationEvent(User user,
                                ChannelType channelType,
                                DeliveryStatus status,
                                String message,
                                int attempts,
                                String errorMessage,
                                Instant occurredAt) {
}
