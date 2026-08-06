package com.example.logging.core;

import com.example.logging.appender.Appender;
import com.example.logging.appender.ConsoleAppender;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Singleton registry and factory for named loggers. */
public class LogManager {

    private final ConcurrentHashMap<String, Logger> loggers = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Appender> rootAppenders = new CopyOnWriteArrayList<>();
    private volatile LogLevel defaultLevel = LogLevel.INFO;
    private volatile Clock clock = Clock.systemUTC();

    private LogManager() {
        rootAppenders.add(new ConsoleAppender());
    }

    /** Initialization-on-demand holder gives lazy, thread-safe Singleton construction in Java. */
    private static class Holder {
        private static final LogManager INSTANCE = new LogManager();
    }

    public static LogManager getInstance() {
        return Holder.INSTANCE;
    }

    /** Factory method: same name always maps to the same cached Logger instance. */
    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name,
                n -> new Logger(n, defaultLevel, List.copyOf(rootAppenders), clock));
    }

    public void configureRoot(LogLevel level, List<Appender> appenders, Clock clock) {
        this.defaultLevel = Objects.requireNonNull(level);
        this.clock = Objects.requireNonNull(clock);
        rootAppenders.clear();
        rootAppenders.addAll(appenders);
    }

    public void resetForTests() {
        loggers.clear();
        rootAppenders.clear();
        rootAppenders.add(new ConsoleAppender());
        defaultLevel = LogLevel.INFO;
        clock = Clock.systemUTC();
    }
}
