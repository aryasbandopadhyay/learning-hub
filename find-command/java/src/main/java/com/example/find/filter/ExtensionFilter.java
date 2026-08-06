package com.example.find.filter;

import com.example.find.model.FileNode;
import com.example.find.model.FileSystemEntry;

/** Matches only files with a normalized extension such as ".txt". */
public class ExtensionFilter implements Filter {

    private final String extension;

    public ExtensionFilter(String extension) {
        this.extension = extension.startsWith(".") ? extension : "." + extension;
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        return entry instanceof FileNode file && file.getExtension().equals(extension);
    }
}
