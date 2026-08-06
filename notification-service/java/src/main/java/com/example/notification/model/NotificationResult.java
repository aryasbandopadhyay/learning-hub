package com.example.notification.model;

/** Per-channel result returned to the caller; failures are represented here, not thrown. */
public record NotificationResult(ChannelType channelType,
                                 DeliveryStatus status,
                                 int attempts,
                                 String errorMessage) {

    public static NotificationResult sent(ChannelType channelType, int attempts) {
        return new NotificationResult(channelType, DeliveryStatus.SENT, attempts, null);
    }

    public static NotificationResult failed(ChannelType channelType, int attempts, String errorMessage) {
        return new NotificationResult(channelType, DeliveryStatus.FAILED, attempts, errorMessage);
    }
}
