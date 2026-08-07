package com.example.filesystem.model;

/**
 * Common abstraction for every node in the file-system tree.
 *
 * <p>This is the base of the Composite pattern: both leaf nodes ({@link FileEntry}) and composite
 * nodes ({@link Directory}) have a name and can be referenced as a {@code FileSystemEntry}. The
 * service can therefore traverse a tree of entries without special casing the parent container.
 */
public abstract class FileSystemEntry {

    private final String name;

    protected FileSystemEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** @return true for leaf file nodes. */
    public abstract boolean isFile();

    /** @return true for composite directory nodes. */
    public boolean isDirectory() {
        return !isFile();
    }
}
