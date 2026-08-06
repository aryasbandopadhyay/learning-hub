package com.example.logging.appender;

import com.example.logging.core.LogRecord;
import com.example.logging.format.Formatter;
import com.example.logging.format.SimpleFormatter;

import java.util.ArrayList;
import java.util.List;

/** Test-friendly sink. All mutations and snapshots are synchronized to avoid lost records. */
public class InMemoryAppender implements Appender {

    private final Formatter formatter;
    private final List<LogRecord> records = new ArrayList<>();
    private final List<String> lines = new ArrayList<>();

    public InMemoryAppender() {
        this(new SimpleFormatter());
    }

    public InMemoryAppender(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public synchronized void append(LogRecord record) {
        records.add(record);
        lines.add(formatter.format(record));
    }

    public synchronized List<LogRecord> records() {
        return List.copyOf(records);
    }

    public synchronized List<String> lines() {
        return List.copyOf(lines);
    }

    public synchronized int size() {
        return records.size();
    }
}
