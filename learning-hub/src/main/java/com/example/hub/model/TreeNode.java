package com.example.hub.model;

import java.util.List;

/**
 * A node in a category's file tree — either a directory (with children) or a file (leaf).
 *
 * @param name     display name (the file/folder name only).
 * @param path     path RELATIVE to the content root, using '/' separators (URL-friendly). This
 *                 is the value the frontend passes back to {@code GET /api/file?path=...}.
 * @param type     {@code "dir"} or {@code "file"}.
 * @param ext      lower-case file extension without the dot (e.g. {@code "java"}); null for dirs.
 * @param children child nodes for directories; null/empty for files.
 */
public record TreeNode(
        String name,
        String path,
        String type,
        String ext,
        List<TreeNode> children
) {
    public static TreeNode dir(String name, String path, List<TreeNode> children) {
        return new TreeNode(name, path, "dir", null, children);
    }

    public static TreeNode file(String name, String path, String ext) {
        return new TreeNode(name, path, "file", ext, null);
    }
}
