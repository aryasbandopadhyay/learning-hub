package com.example.logging.format;

import com.example.logging.core.LogRecord;

/** Strategy interface: different formatters can render the same record differently. */
public interface Formatter {
    String format(LogRecord record);
}
