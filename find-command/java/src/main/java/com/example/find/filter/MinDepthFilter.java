package com.example.find.filter;

import com.example.find.model.FileSystemEntry;

/** Matches only entries at or below a minimum traversal depth; root is depth 0. */
public class MinDepthFilter implements Filter {

    private final int minDepth;

    public MinDepthFilter(int minDepth) {
        if (minDepth < 0) {
            throw new IllegalArgumentException("minDepth must be non-negative");
        }
        this.minDepth = minDepth;
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        return depth >= minDepth;
    }
}
