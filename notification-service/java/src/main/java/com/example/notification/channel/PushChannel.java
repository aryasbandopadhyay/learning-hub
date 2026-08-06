package com.example.notification.channel;

import com.example.notification.model.ChannelType;

import java.time.Clock;

/** Push strategy; fake implementation captures messages for assertions. */
public class PushChannel extends InMemoryChannel {

    public PushChannel(Clock clock) {
        super(ChannelType.PUSH, clock);
    }
}
