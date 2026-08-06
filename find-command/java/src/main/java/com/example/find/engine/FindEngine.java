package com.example.find.engine;

import com.example.find.filter.Filter;
import com.example.find.model.DirectoryNode;
import com.example.find.model.FileSystemEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only DFS traversal engine.
 *
 * <p>This class is intentionally tiny: traversal is separate from matching, and matching is
 * delegated to Filter objects. A Visitor could be introduced later if traversal needed multiple
 * actions (print, delete, exec), but returning matches is enough for this MVP.
 */
public class FindEngine {

    public List<String> find(DirectoryNode root, Filter filter) {
        List<String> result = new ArrayList<>();
        dfs(root, pathForRoot(root), 0, filter, result, null);
        return result;
    }

    public List<FileSystemEntry> findEntries(DirectoryNode root, Filter filter) {
        List<FileSystemEntry> result = new ArrayList<>();
        dfs(root, pathForRoot(root), 0, filter, null, result);
        return result;
    }

    private void dfs(FileSystemEntry entry,
                     String path,
                     int depth,
                     Filter filter,
                     List<String> paths,
                     List<FileSystemEntry> entries) {
        if (filter.matches(entry, depth)) {
            if (paths != null) {
                paths.add(path);
            }
            if (entries != null) {
                entries.add(entry);
            }
        }
        if (entry instanceof DirectoryNode directory) {
            for (FileSystemEntry child : directory.getChildren()) {
                dfs(child, path + "/" + child.getName(), depth + 1, filter, paths, entries);
            }
        }
    }

    private String pathForRoot(DirectoryNode root) {
        return "/" + root.getName();
    }
}
