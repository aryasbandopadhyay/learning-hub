package com.example.find.model;

/**
 * Base type in the Composite pattern.
 *
 * <p>Both files and directories have a name and a size, so the find engine can treat them
 * uniformly while traversing. Directories expose children only in the composite subclass; files
 * remain leaf nodes.
 */
public abstract class FileSystemEntry {

    private final String name;
    private final long sizeBytes;

    protected FileSystemEntry(String name, long sizeBytes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("entry name must be non-empty");
        }
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
        this.name = name;
        this.sizeBytes = sizeBytes;
    }

    public String getName() {
        return name;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public abstract EntryType getType();

    public boolean isFile() {
        return getType() == EntryType.FILE;
    }

    public boolean isDirectory() {
        return getType() == EntryType.DIRECTORY;
    }
}
