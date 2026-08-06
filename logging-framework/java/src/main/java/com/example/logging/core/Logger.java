package com.example.logging.core;

import com.example.logging.appender.Appender;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Named logger with level filtering and fan-out to appenders. */
public class Logger {

    private final String name;
    private final Clock clock;
    private final CopyOnWriteArrayList<Appender> appenders;
    private volatile LogLevel minimumLevel;

    public Logger(String name, LogLevel minimumLevel, List<Appender> appenders, Clock clock) {
        this.name = Objects.requireNonNull(name);
        this.minimumLevel = Objects.requireNonNull(minimumLevel);
        this.appenders = new CopyOnWriteArrayList<>(appenders);
        this.clock = Objects.requireNonNull(clock);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    /** Filter first, build one immutable record, then publish it to each appender. */
    public void log(LogLevel level, String message) {
        if (!level.isAtLeast(minimumLevel)) {
            return;
        }
        LogRecord record = new LogRecord(
                level,
                message,
                name,
                clock.instant(),
                Thread.currentThread().getName());
        for (Appender appender : appenders) {
            appender.append(record);
        }
    }

    public void addAppender(Appender appender) {
        appenders.add(Objects.requireNonNull(appender));
    }

    public void clearAppenders() {
        appenders.clear();
    }

    public void setMinimumLevel(LogLevel minimumLevel) {
        this.minimumLevel = Objects.requireNonNull(minimumLevel);
    }

    public String getName() {
        return name;
    }

    public LogLevel getMinimumLevel() {
        return minimumLevel;
    }
}
