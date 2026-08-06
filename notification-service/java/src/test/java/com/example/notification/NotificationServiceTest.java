package com.example.notification;

import com.example.notification.channel.ChannelFactory;
import com.example.notification.channel.EmailChannel;
import com.example.notification.channel.InMemoryChannel;
import com.example.notification.channel.NotificationChannel;
import com.example.notification.exception.NotificationDeliveryException;
import com.example.notification.model.ChannelType;
import com.example.notification.model.DeliveryStatus;
import com.example.notification.model.NotificationResult;
import com.example.notification.model.User;
import com.example.notification.service.AuditListener;
import com.example.notification.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests mirror the Python suite, including retry and a multi-threaded dispatch race. */
class NotificationServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);

    private User user(EnumSet<ChannelType> preferences) {
        return new User("u1", "Asha", "asha@example.com", "+919999999999", "device-123", preferences);
    }

    @Test
    void notifyRoutesMessageToRequestedChannels() {
        ChannelFactory factory = new ChannelFactory(clock);
        NotificationService service = new NotificationService(factory, 3, clock);

        Map<ChannelType, NotificationResult> results = service.notify(
                user(EnumSet.of(ChannelType.EMAIL)),
                "Hello",
                List.of(ChannelType.EMAIL, ChannelType.SMS));

        EmailChannel email = (EmailChannel) factory.create(ChannelType.EMAIL);
        assertEquals(DeliveryStatus.SENT, results.get(ChannelType.EMAIL).status());
        assertEquals(DeliveryStatus.SENT, results.get(ChannelType.SMS).status());
        assertEquals(1, email.getSentMessages().size());
        assertEquals("asha@example.com", email.getSentMessages().get(0).recipient());
        assertEquals("Hello", email.getSentMessages().get(0).message());
    }

    @Test
    void notifyWithoutChannelsUsesUserPreferences() {
        ChannelFactory factory = new ChannelFactory(clock);
        NotificationService service = new NotificationService(factory, 3, clock);

        Map<ChannelType, NotificationResult> results = service.notify(
                user(EnumSet.of(ChannelType.EMAIL, ChannelType.PUSH)),
                "Preference based");

        assertEquals(EnumSet.of(ChannelType.EMAIL, ChannelType.PUSH), results.keySet());
        assertEquals(1, ((InMemoryChannel) factory.create(ChannelType.EMAIL)).getSentMessages().size());
        assertEquals(0, ((InMemoryChannel) factory.create(ChannelType.SMS)).getSentMessages().size());
        assertEquals(1, ((InMemoryChannel) factory.create(ChannelType.PUSH)).getSentMessages().size());
    }

    @Test
    void retryEventuallySucceedsBeforeMaxAttempts() {
        FailingThenSuccessChannel flaky = new FailingThenSuccessChannel(ChannelType.EMAIL, 2, clock);
        NotificationService service = new NotificationService(
                new ChannelFactory(Map.of(ChannelType.EMAIL, flaky)), 3, clock);

        NotificationResult result = service.notify(
                user(EnumSet.of(ChannelType.EMAIL)), "Retry me").get(ChannelType.EMAIL);

        assertEquals(DeliveryStatus.SENT, result.status());
        assertEquals(3, result.attempts());
        assertEquals(1, flaky.getSentMessages().size());
    }

    @Test
    void retryFailureIsRecordedAndNotThrown() {
        AlwaysFailingChannel failing = new AlwaysFailingChannel(ChannelType.SMS);
        NotificationService service = new NotificationService(
                new ChannelFactory(Map.of(ChannelType.SMS, failing)), 3, clock);

        NotificationResult result = service.notify(
                user(EnumSet.of(ChannelType.SMS)), "Will fail").get(ChannelType.SMS);

        assertEquals(DeliveryStatus.FAILED, result.status());
        assertEquals(3, result.attempts());
        assertNotNull(result.errorMessage());
        assertEquals(3, failing.attempts.get());
    }

    @Test
    void observerReceivesSentAndFailedEvents() {
        AlwaysFailingChannel failing = new AlwaysFailingChannel(ChannelType.SMS);
        ChannelFactory factory = new ChannelFactory(clock);
        factory.register(failing);
        NotificationService service = new NotificationService(factory, 2, clock);
        AuditListener audit = new AuditListener();
        service.registerListener(audit);

        service.notify(user(EnumSet.of(ChannelType.EMAIL, ChannelType.SMS)), "Observe");

        assertEquals(2, audit.getEvents().size());
        assertTrue(audit.getEvents().stream().anyMatch(e -> e.status() == DeliveryStatus.SENT));
        assertTrue(audit.getEvents().stream().anyMatch(e -> e.status() == DeliveryStatus.FAILED));
    }

    /**
     * Concurrency test: many threads send one email each through the same service/channel/listener.
     * Thread-safe sinks must record exactly N sends and exactly N observer events.
     */
    @Test
    void concurrentNotificationsAreRecordedExactlyOnce() throws InterruptedException {
        int threads = 50;
        ChannelFactory factory = new ChannelFactory(clock);
        NotificationService service = new NotificationService(factory, 3, clock);
        AuditListener audit = new AuditListener();
        service.registerListener(audit);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await();
                    service.notify(user(EnumSet.of(ChannelType.EMAIL)), "Message " + id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        EmailChannel email = (EmailChannel) factory.create(ChannelType.EMAIL);
        assertEquals(threads, email.getSentMessages().size());
        assertEquals(threads, email.getSentMessages().stream().map(m -> m.message()).distinct().count());
        assertEquals(threads, audit.getEvents().size());
    }

    private static class FailingThenSuccessChannel extends InMemoryChannel {
        private final AtomicInteger remainingFailures;

        FailingThenSuccessChannel(ChannelType type, int failures, Clock clock) {
            super(type, clock);
            this.remainingFailures = new AtomicInteger(failures);
        }

        @Override
        public void send(String recipient, String message) {
            if (remainingFailures.getAndDecrement() > 0) {
                throw new NotificationDeliveryException("temporary failure");
            }
            super.send(recipient, message);
        }
    }

    private static class AlwaysFailingChannel implements NotificationChannel {
        private final ChannelType type;
        private final AtomicInteger attempts = new AtomicInteger();

        AlwaysFailingChannel(ChannelType type) {
            this.type = type;
        }

        @Override
        public ChannelType type() {
            return type;
        }

        @Override
        public void send(String recipient, String message) {
            attempts.incrementAndGet();
            throw new NotificationDeliveryException("downstream unavailable");
        }
    }
}
