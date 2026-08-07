package com.example.filesystem.model;

/**
 * Leaf node in the Composite tree: a file stores append-only text content.
 *
 * <p>The content is a {@link StringBuilder} because LeetCode's API appends repeatedly. The
 * containing {@code InMemoryFileSystem} write lock serializes appends, so callers never observe a
 * partially-written value.
 */
public class FileEntry extends FileSystemEntry {

    private final StringBuilder content = new StringBuilder();

    public FileEntry(String name) {
        super(name);
    }

    @Override
    public boolean isFile() {
        return true;
    }

    public void append(String text) {
        content.append(text);
    }

    public String read() {
        return content.toString();
    }
}
