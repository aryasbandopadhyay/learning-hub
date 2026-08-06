package com.example.notification.channel;

import com.example.notification.model.ChannelType;

import java.time.Clock;

/** SMS strategy; fake implementation captures messages for assertions. */
public class SmsChannel extends InMemoryChannel {

    public SmsChannel(Clock clock) {
        super(ChannelType.SMS, clock);
    }
}
