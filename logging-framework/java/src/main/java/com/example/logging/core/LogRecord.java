package com.example.logging.core;

import java.time.Instant;

/** Immutable value object created once per accepted log message. */
public record LogRecord(LogLevel level,
                        String message,
                        String loggerName,
                        Instant timestamp,
                        String threadName) {
}
