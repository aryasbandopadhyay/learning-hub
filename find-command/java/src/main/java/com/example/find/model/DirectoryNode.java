package com.example.find.model;

import com.example.find.exception.InvalidFileSystemException;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite node: a directory can contain files and other directories.
 *
 * <p>The public children view is copied so callers cannot mutate the tree while a search is
 * running. Machine-coding note: keeping the tree effectively immutable makes locking unnecessary
 * for this read-only MVP.
 */
public class DirectoryNode extends FileSystemEntry {

    private final List<FileSystemEntry> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name, 0);
    }

    public DirectoryNode addChild(FileSystemEntry child) {
        if (child == null) {
            throw new InvalidFileSystemException("child cannot be null");
        }
        boolean duplicate = children.stream().anyMatch(e -> e.getName().equals(child.getName()));
        if (duplicate) {
            throw new InvalidFileSystemException("duplicate child name: " + child.getName());
        }
        children.add(child);
        return this;
    }

    public List<FileSystemEntry> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public EntryType getType() {
        return EntryType.DIRECTORY;
    }
}
