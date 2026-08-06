package com.example.find.filter;

import com.example.find.model.EntryType;
import com.example.find.model.FileSystemEntry;

/** Matches files or directories, just like `find -type f` / `find -type d`. */
public class TypeFilter implements Filter {

    private final EntryType type;

    public TypeFilter(EntryType type) {
        this.type = type;
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        return entry.getType() == type;
    }
}
