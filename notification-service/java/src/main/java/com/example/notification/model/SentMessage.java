package com.example.notification.model;

import java.time.Instant;

/** Immutable record kept by fake in-memory channels so tests can assert without real providers. */
public record SentMessage(ChannelType channelType, String recipient, String message, Instant sentAt) {
}
