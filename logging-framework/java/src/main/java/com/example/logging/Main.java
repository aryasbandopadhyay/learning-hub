package com.example.logging;

import com.example.logging.appender.ConsoleAppender;
import com.example.logging.appender.InMemoryAppender;
import com.example.logging.core.LogLevel;
import com.example.logging.core.LogManager;
import com.example.logging.core.Logger;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

/** Runnable demo showing filtering, fan-out, and LogManager's named logger cache. */
public class Main {

    public static void main(String[] args) {
        Thread.currentThread().setName("MainThread");
        LogManager manager = LogManager.getInstance();
        InMemoryAppender audit = new InMemoryAppender();
        manager.configureRoot(
                LogLevel.INFO,
                List.of(new ConsoleAppender(), audit),
                Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC));

        Logger logger = manager.getLogger("checkout");
        logger.debug("debug details are below INFO and are dropped");
        logger.info("order created");
        logger.warn("payment retry scheduled");
        logger.error("payment failed");

        System.out.println("In-memory records: " + audit.size());
    }
}
