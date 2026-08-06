package com.example.notification;

import com.example.notification.channel.ChannelFactory;
import com.example.notification.channel.EmailChannel;
import com.example.notification.channel.PushChannel;
import com.example.notification.model.ChannelType;
import com.example.notification.model.User;
import com.example.notification.service.AuditListener;
import com.example.notification.service.NotificationService;

import java.time.Clock;
import java.util.EnumSet;
import java.util.List;

/** Runnable demo: build the service, send one notification, and print captured deliveries. */
public class Main {

    public static void main(String[] args) {
        Clock clock = Clock.systemUTC();
        ChannelFactory factory = new ChannelFactory(clock);
        NotificationService service = new NotificationService(factory, 3, clock);
        AuditListener audit = new AuditListener();
        service.registerListener(audit);

        User user = new User(
                "u1",
                "Asha",
                "asha@example.com",
                "+919999999999",
                "device-123",
                EnumSet.of(ChannelType.EMAIL, ChannelType.PUSH));

        var results = service.notify(user, "Your order has shipped");
        System.out.println("Results: " + results);
        System.out.println("Email sent: " + ((EmailChannel) factory.create(ChannelType.EMAIL)).getSentMessages().size());
        System.out.println("Push sent: " + ((PushChannel) factory.create(ChannelType.PUSH)).getSentMessages().size());
        System.out.println("Audit events: " + audit.getEvents().size());
        System.out.println("Channels used: " + List.copyOf(results.keySet()));
    }
}
