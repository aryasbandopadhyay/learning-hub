package com.example.hub.model;

/**
 * A lightweight view of a category for the tab bar, returned by {@code GET /api/categories}.
 * (We don't expose the raw filesystem {@code paths} to the client — only what the UI needs.)
 *
 * @param id          URL-safe identifier used in subsequent API calls.
 * @param label       label to show on the tab.
 * @param description optional blurb shown above the tree (may be null/blank).
 */
public record CategoryDto(
        String id,
        String label,
        String description
) {
}
