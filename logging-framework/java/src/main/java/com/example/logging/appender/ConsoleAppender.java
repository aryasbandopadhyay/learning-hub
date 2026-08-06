package com.example.logging.appender;

import com.example.logging.core.LogRecord;
import com.example.logging.format.Formatter;
import com.example.logging.format.SimpleFormatter;

/** Console sink. Synchronization keeps lines from different threads from interleaving. */
public class ConsoleAppender implements Appender {

    private final Formatter formatter;

    public ConsoleAppender() {
        this(new SimpleFormatter());
    }

    public ConsoleAppender(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public synchronized void append(LogRecord record) {
        System.out.println(formatter.format(record));
    }
}
