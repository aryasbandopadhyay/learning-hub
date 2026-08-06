package com.example.find.filter;

import com.example.find.model.FileSystemEntry;

import java.util.Arrays;
import java.util.List;

/** Combines specifications with logical OR; any child may match. */
public class OrFilter implements Filter {

    private final List<Filter> filters;

    public OrFilter(Filter... filters) {
        this.filters = List.copyOf(Arrays.asList(filters));
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        return filters.stream().anyMatch(f -> f.matches(entry, depth));
    }
}
