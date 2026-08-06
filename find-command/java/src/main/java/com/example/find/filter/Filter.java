package com.example.find.filter;

import com.example.find.model.FileSystemEntry;

/**
 * Specification pattern abstraction.
 *
 * <p>Each implementation answers one tiny question, such as "does the name match?" or "is the
 * size greater than 10 KB?". The engine only knows this interface, so filters compose freely.
 */
public interface Filter {
    boolean matches(FileSystemEntry entry, int depth);

    default boolean matches(FileSystemEntry entry) {
        return matches(entry, 0);
    }
}
