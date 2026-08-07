package com.example.filesystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Composite node: a directory owns a sorted map of child entries.
 *
 * <p>{@link TreeMap} keeps child names lexicographic at all times, so {@code ls(directory)} is a
 * cheap snapshot of keys and is deterministic for tests/demo output. Thread-safety is provided by
 * the service-level read/write lock; this model stays intentionally small.
 */
public class Directory extends FileSystemEntry {

    private final NavigableMap<String, FileSystemEntry> children = new TreeMap<>();

    public Directory(String name) {
        super(name);
    }

    @Override
    public boolean isFile() {
        return false;
    }

    public FileSystemEntry getChild(String name) {
        return children.get(name);
    }

    public void putChild(FileSystemEntry entry) {
        children.put(entry.getName(), entry);
    }

    public List<String> listNames() {
        return new ArrayList<>(children.keySet());
    }
}
