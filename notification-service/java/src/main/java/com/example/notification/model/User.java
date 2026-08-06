package com.example.notification.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Recipient profile plus notification preferences.
 *
 * <p>Preferences are stored as an {@link EnumSet}: compact, immutable after construction here, and
 * perfect for an enum-backed MVP. The service uses these defaults when the caller does not pass an
 * explicit channel list.
 */
public class User {

    private final String id;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String deviceToken;
    private final Set<ChannelType> preferredChannels;

    public User(String id,
                String name,
                String email,
                String phoneNumber,
                String deviceToken,
                Set<ChannelType> preferredChannels) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;
        this.preferredChannels = preferredChannels.isEmpty()
                ? EnumSet.noneOf(ChannelType.class)
                : EnumSet.copyOf(preferredChannels);
    }

    public String recipientFor(ChannelType type) {
        return switch (type) {
            case EMAIL -> email;
            case SMS -> phoneNumber;
            case PUSH -> deviceToken;
        };
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<ChannelType> getPreferredChannels() {
        return EnumSet.copyOf(preferredChannels);
    }
}
