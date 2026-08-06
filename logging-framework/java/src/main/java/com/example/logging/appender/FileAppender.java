package com.example.logging.appender;

import com.example.logging.core.LogRecord;
import com.example.logging.exception.LoggingException;
import com.example.logging.format.Formatter;
import com.example.logging.format.SimpleFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** File sink. The synchronized append method serializes writes from many logging threads. */
public class FileAppender implements Appender {

    private final Path path;
    private final Formatter formatter;

    public FileAppender(Path path) {
        this(path, new SimpleFormatter());
    }

    public FileAppender(Path path, Formatter formatter) {
        this.path = path;
        this.formatter = formatter;
    }

    @Override
    public synchronized void append(LogRecord record) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path,
                    formatter.format(record) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new LoggingException("Failed to write log file: " + path, e);
        }
    }
}
