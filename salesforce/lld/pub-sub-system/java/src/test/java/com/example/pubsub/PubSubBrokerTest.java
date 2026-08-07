package com.example.pubsub;

import com.example.pubsub.service.PubSubBroker;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PubSubBrokerTest {

    private PubSubBroker newBroker() {
        return new PubSubBroker(Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void publishDeliversToMultipleSubscribers() throws InterruptedException {
        PubSubBroker broker = newBroker();
        try {
            CountDownLatch delivered = new CountDownLatch(2);
            List<String> receivedByA = new ArrayList<>();
            List<String> receivedByB = new ArrayList<>();

            broker.subscribe("orders", "a", message -> {
                receivedByA.add(message.getPayload());
                delivered.countDown();
            });
            broker.subscribe("orders", "b", message -> {
                receivedByB.add(message.getPayload());
                delivered.countDown();
            });

            broker.publish("orders", "created");

            assertTrue(delivered.await(5, TimeUnit.SECONDS));
            assertEquals(List.of("created"), receivedByA);
            assertEquals(List.of("created"), receivedByB);
        } finally {
            broker.shutdown();
        }
    }

    @Test
    void unsubscribeStopsFutureDelivery() throws InterruptedException {
        PubSubBroker broker = newBroker();
        try {
            CountDownLatch firstDelivery = new CountDownLatch(1);
            CountDownLatch unexpectedSecondDelivery = new CountDownLatch(1);
            List<String> received = new ArrayList<>();

            broker.subscribe("orders", "email", message -> {
                received.add(message.getPayload());
                if ("before-unsubscribe".equals(message.getPayload())) {
                    firstDelivery.countDown();
                } else {
                    unexpectedSecondDelivery.countDown();
                }
            });

            broker.publish("orders", "before-unsubscribe");
            assertTrue(firstDelivery.await(5, TimeUnit.SECONDS));
            broker.unsubscribe("orders", "email");
            broker.publish("orders", "after-unsubscribe");

            assertTrue(!unexpectedSecondDelivery.await(300, TimeUnit.MILLISECONDS));
            assertEquals(List.of("before-unsubscribe"), received);
        } finally {
            broker.shutdown();
        }
    }

    @Test
    void eachSubscriberReceivesEachMessageExactlyOnce() throws InterruptedException {
        PubSubBroker broker = newBroker();
        try {
            int messages = 25;
            CountDownLatch delivered = new CountDownLatch(messages);
            Set<Long> offsets = ConcurrentHashMap.newKeySet();

            broker.subscribe("orders", "email", message -> {
                offsets.add(message.getOffset());
                delivered.countDown();
            });

            for (int i = 0; i < messages; i++) {
                broker.publish("orders", "event-" + i);
            }

            assertTrue(delivered.await(5, TimeUnit.SECONDS));
            assertEquals(messages, offsets.size());
            assertEquals(Set.copyOf(range(messages)), offsets);
            assertEquals(messages, broker.nextOffset("orders", "email"));
        } finally {
            broker.shutdown();
        }
    }

    @Test
    void concurrentPublishersDeliverAllMessagesToAllSubscribers() throws InterruptedException {
        PubSubBroker broker = newBroker();
        try {
            int publishers = 8;
            int perPublisher = 25;
            int total = publishers * perPublisher;
            CountDownLatch delivered = new CountDownLatch(total * 2);
            Set<String> emailPayloads = ConcurrentHashMap.newKeySet();
            Set<String> analyticsPayloads = ConcurrentHashMap.newKeySet();
            Set<Long> offsets = ConcurrentHashMap.newKeySet();

            broker.subscribe("orders", "email", message -> {
                emailPayloads.add(message.getPayload());
                offsets.add(message.getOffset());
                delivered.countDown();
            });
            broker.subscribe("orders", "analytics", message -> {
                analyticsPayloads.add(message.getPayload());
                delivered.countDown();
            });

            ExecutorService pool = Executors.newFixedThreadPool(publishers);
            CountDownLatch start = new CountDownLatch(1);
            for (int p = 0; p < publishers; p++) {
                final int publisherId = p;
                pool.submit(() -> {
                    try {
                        start.await();
                        for (int i = 0; i < perPublisher; i++) {
                            broker.publish("orders", "p" + publisherId + "-m" + i);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            start.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

            assertTrue(delivered.await(10, TimeUnit.SECONDS));
            assertEquals(total, emailPayloads.size());
            assertEquals(total, analyticsPayloads.size());
            assertEquals(emailPayloads, analyticsPayloads);
            assertEquals(total, offsets.size());
        } finally {
            broker.shutdown();
        }
    }

    private static Set<Long> range(int endExclusive) {
        Set<Long> values = new HashSet<>();
        for (long i = 0; i < endExclusive; i++) {
            values.add(i);
        }
        return values;
    }
}
