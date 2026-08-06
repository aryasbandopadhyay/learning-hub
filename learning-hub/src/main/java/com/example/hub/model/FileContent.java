package com.example.hub.model;

/**
 * The content of a single file, returned by {@code GET /api/file}.
 *
 * @param category  the category id the file was read from.
 * @param path      path relative to the content root ('/'-separated).
 * @param name      the file name only.
 * @param ext       lower-case extension without the dot (drives syntax highlighting on the client).
 * @param markdown  true if this is a Markdown file — the frontend renders it (Mermaid diagrams
 *                  included) instead of showing raw code.
 * @param content   the raw UTF-8 text of the file.
 */
public record FileContent(
        String category,
        String path,
        String name,
        String ext,
        boolean markdown,
        String content
) {
}
