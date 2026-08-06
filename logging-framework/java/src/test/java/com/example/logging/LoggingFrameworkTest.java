package com.example.logging;

import com.example.logging.appender.InMemoryAppender;
import com.example.logging.core.LogLevel;
import com.example.logging.core.LogManager;
import com.example.logging.core.Logger;
import com.example.logging.format.SimpleFormatter;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingFrameworkTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);

    private Logger newLogger(LogLevel level, InMemoryAppender... appenders) {
        return new Logger("orders", level, List.of(appenders), FIXED_CLOCK);
    }

    @Test
    void levelFilteringDropsBelowThreshold() {
        InMemoryAppender memory = new InMemoryAppender();
        Logger logger = newLogger(LogLevel.WARN, memory);

        logger.debug("hidden debug");
        logger.info("hidden info");
        logger.warn("visible warn");
        logger.error("visible error");

        assertEquals(2, memory.size());
        assertEquals(LogLevel.WARN, memory.records().get(0).level());
        assertEquals(LogLevel.ERROR, memory.records().get(1).level());
    }

    @Test
    void simpleFormatterIncludesExpectedFields() {
        InMemoryAppender memory = new InMemoryAppender(new SimpleFormatter());
        Logger logger = newLogger(LogLevel.DEBUG, memory);

        logger.info("created");

        String line = memory.lines().get(0);
        assertTrue(line.startsWith("[2024-01-01T10:00:00Z] INFO orders ["));
        assertTrue(line.endsWith("] - created"));
    }

    @Test
    void multipleAppendersEachReceiveTheRecord() {
        InMemoryAppender first = new InMemoryAppender();
        InMemoryAppender second = new InMemoryAppender();
        Logger logger = newLogger(LogLevel.INFO, first, second);

        logger.info("fan out");

        assertEquals(1, first.size());
        assertEquals(1, second.size());
        assertEquals("fan out", first.records().get(0).message());
        assertEquals("fan out", second.records().get(0).message());
    }

    @Test
    void logManagerReturnsSameLoggerForSameName() {
        LogManager manager = LogManager.getInstance();
        manager.resetForTests();

        Logger a = manager.getLogger("billing");
        Logger b = manager.getLogger("billing");

        assertSame(a, b);
    }

    /** Many threads write through one logger; the synchronized appender must capture every record. */
    @Test
    void concurrentLoggingDoesNotLoseRecords() throws InterruptedException {
        int threads = 20;
        int messagesPerThread = 100;
        InMemoryAppender memory = new InMemoryAppender();
        Logger logger = newLogger(LogLevel.DEBUG, memory);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < threads; i++) {
            final int workerId = i;
            pool.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < messagesPerThread; j++) {
                        logger.info("worker-" + workerId + " message-" + j);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(threads * messagesPerThread, memory.size());
    }
}
