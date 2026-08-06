package com.example.find.model;

/** Leaf node in the Composite pattern. A file has content size and an extension. */
public class FileNode extends FileSystemEntry {

    private final String extension;

    public FileNode(String name, long sizeBytes) {
        super(name, sizeBytes);
        this.extension = extractExtension(name);
    }

    private static String extractExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot <= 0 || dot == name.length() - 1) {
            return "";
        }
        return name.substring(dot);
    }

    @Override
    public EntryType getType() {
        return EntryType.FILE;
    }

    public String getExtension() {
        return extension;
    }
}
