package com.example.pubsub;

import com.example.pubsub.model.Message;
import com.example.pubsub.service.PubSubBroker;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Runnable demo showing the end-to-end flow: create a topic, attach observers, publish events, then
 * unsubscribe one observer. The callbacks record lines and the main thread prints them in a fixed
 * order, so the output stays deterministic even though delivery is asynchronous.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        PubSubBroker broker = new PubSubBroker(
                Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC));
        try {
            broker.createTopic("orders");
            System.out.println("Created topic orders");

            List<String> emailLines = Collections.synchronizedList(new ArrayList<>());
            List<String> analyticsLines = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch firstMessage = new CountDownLatch(2);
            CountDownLatch secondMessage = new CountDownLatch(2);

            broker.subscribe("orders", "email-service", message -> {
                emailLines.add(format("email-service", message));
                if (message.getOffset() == 0) {
                    firstMessage.countDown();
                } else if (message.getOffset() == 1) {
                    secondMessage.countDown();
                }
            });
            System.out.println("Subscribed email-service to orders");

            broker.subscribe("orders", "analytics-service", message -> {
                analyticsLines.add(format("analytics-service", message));
                if (message.getOffset() == 0) {
                    firstMessage.countDown();
                } else if (message.getOffset() == 1) {
                    secondMessage.countDown();
                }
            });
            System.out.println("Subscribed analytics-service to orders");

            Message m0 = broker.publish("orders", "order-1-created");
            System.out.println("Published orders#" + m0.getOffset() + ": " + m0.getPayload());
            firstMessage.await(5, TimeUnit.SECONDS);
            System.out.println(emailLines.get(0));
            System.out.println(analyticsLines.get(0));

            Message m1 = broker.publish("orders", "order-2-paid");
            System.out.println("Published orders#" + m1.getOffset() + ": " + m1.getPayload());
            secondMessage.await(5, TimeUnit.SECONDS);
            System.out.println(emailLines.get(1));
            System.out.println(analyticsLines.get(1));

            broker.unsubscribe("orders", "analytics-service");
            System.out.println("Unsubscribed analytics-service");

            CountDownLatch lastMessage = new CountDownLatch(1);
            broker.subscribe("orders", "email-service", message -> {
                emailLines.add(format("email-service", message));
                lastMessage.countDown();
            });
            Message m2 = broker.publish("orders", "order-3-shipped");
            System.out.println("Published orders#" + m2.getOffset() + ": " + m2.getPayload());
            lastMessage.await(5, TimeUnit.SECONDS);
            System.out.println(emailLines.get(2));
        } finally {
            broker.shutdown();
        }
    }

    private static String format(String subscriberName, Message message) {
        return subscriberName + " received " + message.getTopicName() + "#" + message.getOffset()
                + " -> " + message.getPayload();
    }
}
