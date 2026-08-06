package com.example.notification.channel;

import com.example.notification.model.ChannelType;

import java.time.Clock;

/** Email strategy; fake implementation captures messages for assertions. */
public class EmailChannel extends InMemoryChannel {

    public EmailChannel(Clock clock) {
        super(ChannelType.EMAIL, clock);
    }
}
