package com.example.logging.appender;

import com.example.logging.core.LogRecord;

/** Observer-like sink. A logger fans one accepted record out to every attached appender. */
public interface Appender {
    void append(LogRecord record);
}
