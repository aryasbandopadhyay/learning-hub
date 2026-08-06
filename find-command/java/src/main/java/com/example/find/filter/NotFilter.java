package com.example.find.filter;

import com.example.find.model.FileSystemEntry;

/** Negates a specification, mirroring `find ! <expression>`. */
public class NotFilter implements Filter {

    private final Filter delegate;

    public NotFilter(Filter delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        return !delegate.matches(entry, depth);
    }
}
