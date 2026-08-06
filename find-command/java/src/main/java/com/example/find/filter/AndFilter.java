package com.example.find.filter;

import com.example.find.model.FileSystemEntry;

import java.util.Arrays;
import java.util.List;

/** Combines specifications with logical AND; all children must match. */
public class AndFilter implements Filter {

    private final List<Filter> filters;

    public AndFilter(Filter... filters) {
        this.filters = List.copyOf(Arrays.asList(filters));
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        return filters.stream().allMatch(f -> f.matches(entry, depth));
    }
}
