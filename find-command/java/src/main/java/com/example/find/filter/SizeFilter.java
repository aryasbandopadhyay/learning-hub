package com.example.find.filter;

import com.example.find.model.FileSystemEntry;

/** Matches entries by size using either min/max bounds or a single comparison operator. */
public class SizeFilter implements Filter {

    private final Long minInclusive;
    private final Long maxInclusive;
    private final SizeComparison comparison;
    private final Long compareTo;

    public SizeFilter(Long minInclusive, Long maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.comparison = null;
        this.compareTo = null;
    }

    public SizeFilter(SizeComparison comparison, long compareTo) {
        this.minInclusive = null;
        this.maxInclusive = null;
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    public static SizeFilter greaterThan(long bytes) {
        return new SizeFilter(SizeComparison.GREATER_THAN, bytes);
    }

    public static SizeFilter lessThan(long bytes) {
        return new SizeFilter(SizeComparison.LESS_THAN, bytes);
    }

    public static SizeFilter equalTo(long bytes) {
        return new SizeFilter(SizeComparison.EQUAL_TO, bytes);
    }

    public static SizeFilter between(long minInclusive, long maxInclusive) {
        return new SizeFilter(minInclusive, maxInclusive);
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        long size = entry.getSizeBytes();
        if (comparison == SizeComparison.GREATER_THAN) {
            return size > compareTo;
        }
        if (comparison == SizeComparison.LESS_THAN) {
            return size < compareTo;
        }
        if (comparison == SizeComparison.EQUAL_TO) {
            return size == compareTo;
        }
        return (minInclusive == null || size >= minInclusive)
                && (maxInclusive == null || size <= maxInclusive);
    }
}
