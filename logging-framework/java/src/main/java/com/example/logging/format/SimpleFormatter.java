package com.example.logging.format;

import com.example.logging.core.LogRecord;

/** Human-readable formatter used by the demo and tests. */
public class SimpleFormatter implements Formatter {

    @Override
    public String format(LogRecord record) {
        return "[" + record.timestamp() + "] "
                + record.level() + " "
                + record.loggerName() + " [" + record.threadName() + "] - "
                + record.message();
    }
}
