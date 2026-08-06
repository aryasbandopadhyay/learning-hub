package com.example.logging.core;

/** Ordered severity levels. The enum order intentionally models DEBUG < INFO < WARN < ERROR. */
public enum LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR;

    /** Return true when this message level should pass a logger configured at {@code threshold}. */
    public boolean isAtLeast(LogLevel threshold) {
        return this.ordinal() >= threshold.ordinal();
    }
}
