package com.example.filesystem.service;

import com.example.filesystem.model.Directory;
import com.example.filesystem.model.FileEntry;
import com.example.filesystem.model.FileSystemEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe in-memory file-system service.
 *
 * <p>The tree root is a {@link Directory}; every path lookup walks child entries from that root.
 * Public operations take a read/write lock around the whole traversal so a reader never sees a
 * half-created path and writers cannot corrupt the Composite tree while creating directories/files.
 */
public class InMemoryFileSystem {

    private final Directory root = new Directory("");
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * LeetCode 588 behavior: if {@code path} is a file return only its name; if it is a directory
     * return its child names sorted lexicographically.
     */
    public List<String> ls(String path) {
        lock.readLock().lock();
        try {
            FileSystemEntry entry = traverse(path);
            if (entry instanceof FileEntry) {
                return List.of(entry.getName());
            }
            return ((Directory) entry).listNames();
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Create all missing directories along the absolute path. */
    public void mkdir(String path) {
        lock.writeLock().lock();
        try {
            directoryFor(path, true);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Create the file if absent, then append content to it. Missing parent directories are made. */
    public void addContentToFile(String filePath, String content) {
        lock.writeLock().lock();
        try {
            List<String> parts = parts(filePath);
            if (parts.isEmpty()) {
                throw new IllegalArgumentException("File path must not be root");
            }
            Directory parent = directoryFor(String.join("/", parts.subList(0, parts.size() - 1)), true);
            String fileName = parts.get(parts.size() - 1);
            FileSystemEntry existing = parent.getChild(fileName);
            FileEntry file;
            if (existing == null) {
                file = new FileEntry(fileName);
                parent.putChild(file);
            } else if (existing instanceof FileEntry) {
                file = (FileEntry) existing;
            } else {
                throw new IllegalArgumentException(filePath + " is a directory, not a file");
            }
            file.append(content);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Read the full file content. */
    public String readContentFromFile(String filePath) {
        lock.readLock().lock();
        try {
            FileSystemEntry entry = traverse(filePath);
            if (!(entry instanceof FileEntry)) {
                throw new IllegalArgumentException(filePath + " is not a file");
            }
            return ((FileEntry) entry).read();
        } finally {
            lock.readLock().unlock();
        }
    }

    private FileSystemEntry traverse(String path) {
        FileSystemEntry current = root;
        for (String part : parts(path)) {
            if (!(current instanceof Directory)) {
                throw new IllegalArgumentException("Cannot traverse through file: " + current.getName());
            }
            current = ((Directory) current).getChild(part);
            if (current == null) {
                throw new IllegalArgumentException("Path does not exist: " + path);
            }
        }
        return current;
    }

    private Directory directoryFor(String path, boolean create) {
        Directory current = root;
        for (String part : parts(path)) {
            FileSystemEntry child = current.getChild(part);
            if (child == null) {
                if (!create) {
                    throw new IllegalArgumentException("Path does not exist: " + path);
                }
                child = new Directory(part);
                current.putChild(child);
            }
            if (!(child instanceof Directory)) {
                throw new IllegalArgumentException(part + " is a file, not a directory");
            }
            current = (Directory) child;
        }
        return current;
    }

    /** Normalize absolute paths by ignoring empty segments, so '/', '//a//b', and 'a/b' are safe. */
    private List<String> parts(String path) {
        String[] raw = path.split("/");
        List<String> result = new ArrayList<>();
        for (String p : raw) {
            if (!p.isBlank()) {
                result.add(p);
            }
        }
        return result;
    }
}
