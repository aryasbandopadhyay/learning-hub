package com.example.hub.service;

import com.example.hub.config.ContentProperties;
import com.example.hub.model.CategoryDto;
import com.example.hub.model.FileContent;
import com.example.hub.model.TreeNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

/**
 * ============================================================================================
 * ContentService — the heart of the app.
 * ============================================================================================
 * Responsibilities:
 *   1. Resolve the content root (parent of the working dir by default).
 *   2. Expose the configured categories (subject-area tabs).
 *   3. Build a file tree for a category by walking its configured paths.
 *   4. Read a single file safely (path-traversal protected, extension allow-listed, size capped).
 *
 * This is a {@code @Service} — a Spring-managed singleton bean discovered by component scanning
 * and injected wherever needed (here, into the controller) via constructor injection (DI).
 *
 * SECURITY MODEL: a request can only ever read a file that is BOTH (a) inside the content root
 * AND (b) inside one of the requesting category's configured {@code paths}. All paths are
 * normalized and checked with {@code startsWith} so {@code ../} escapes are rejected.
 */
@Service
public class ContentService {

    /** Directory names we never descend into (build output, VCS, caches, IDE metadata). */
    private static final Set<String> EXCLUDED_DIRS = Set.of(
            "target", "build", "bin", "obj", "out",
            ".git", ".idea", ".vscode", ".mvn", ".settings",
            "__pycache__", ".pytest_cache", "node_modules", ".gradle", ".venv", "venv"
    );

    /** Only files with these extensions are listed/served (keeps binaries and junk out). */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "md", "markdown", "txt",
            "java", "py", "kt", "scala", "groovy",
            "js", "ts", "jsx", "tsx",
            "go", "rs", "rb", "php", "swift",
            "c", "cpp", "cc", "h", "hpp", "cs",
            "xml", "yml", "yaml", "properties", "json", "toml", "ini",
            "sql", "sh", "bat", "ps1", "gradle", "css", "html"
    );

    /** Markdown extensions get rendered (not shown as raw code). */
    private static final Set<String> MARKDOWN_EXTENSIONS = Set.of("md", "markdown");

    /** Refuse to read anything larger than this (defensive; our files are tiny). */
    private static final long MAX_FILE_BYTES = 4L * 1024 * 1024; // 4 MB

    private final ContentProperties properties;
    private final Path root;

    /**
     * Constructor injection: Spring passes the bound {@link ContentProperties} bean in. We resolve
     * the content root ONCE here so every later request is cheap and consistent.
     */
    public ContentService(ContentProperties properties) {
        this.properties = properties;
        this.root = resolveRoot(properties.root());
    }

    /** The absolute, normalized content root (useful for diagnostics / the README endpoint). */
    public Path root() {
        return root;
    }

    /** If {@code configuredRoot} is blank, use the PARENT of the working directory. */
    private static Path resolveRoot(String configuredRoot) {
        Path base;
        if (configuredRoot == null || configuredRoot.isBlank()) {
            Path workingDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
            Path parent = workingDir.getParent();
            base = (parent != null) ? parent : workingDir;
        } else {
            base = Paths.get(configuredRoot).toAbsolutePath();
        }
        return base.normalize();
    }

    // -----------------------------------------------------------------------------------------
    // Categories
    // -----------------------------------------------------------------------------------------

    /** The tab list. Order follows application.yml. */
    public List<CategoryDto> categories() {
        List<CategoryDto> result = new ArrayList<>();
        for (ContentProperties.Category c : safeCategories()) {
            result.add(new CategoryDto(c.id(), c.label(), c.description()));
        }
        return result;
    }

    private List<ContentProperties.Category> safeCategories() {
        return properties.categories() == null ? List.of() : properties.categories();
    }

    private ContentProperties.Category requireCategory(String id) {
        return safeCategories().stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unknown category: " + id));
    }

    // -----------------------------------------------------------------------------------------
    // Tree
    // -----------------------------------------------------------------------------------------

    /**
     * Build the file tree for a category. The returned node is a synthetic root whose children are
     * the category's configured paths (each a file or a recursively-walked directory).
     */
    public TreeNode tree(String categoryId) {
        ContentProperties.Category category = requireCategory(categoryId);
        List<TreeNode> children = new ArrayList<>();
        for (String rel : nonNull(category.paths())) {
            Path abs = safeResolve(rel);
            if (abs == null || !Files.exists(abs)) {
                continue; // silently skip missing entries so a typo doesn't break the whole tab
            }
            TreeNode node = Files.isDirectory(abs) ? walk(abs) : fileNode(abs);
            if (node != null) {
                children.add(node);
            }
        }
        return TreeNode.dir(category.label(), "", children);
    }

    /** Recursively walk a directory into a {@link TreeNode}, dirs first then files, both sorted. */
    private TreeNode walk(Path dir) {
        List<TreeNode> children = new ArrayList<>();
        try (Stream<Path> entries = Files.list(dir)) {
            List<Path> sorted = entries
                    .sorted(Comparator
                            .comparing((Path p) -> Files.isDirectory(p) ? 0 : 1)   // dirs before files
                            .thenComparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .toList();
            for (Path p : sorted) {
                String name = p.getFileName().toString();
                if (Files.isDirectory(p)) {
                    if (EXCLUDED_DIRS.contains(name)) {
                        continue;
                    }
                    TreeNode child = walk(p);
                    // Hide empty directories (nothing worth showing after filtering).
                    if (child.children() != null && !child.children().isEmpty()) {
                        children.add(child);
                    }
                } else if (isAllowedFile(name)) {
                    children.add(fileNode(p));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return TreeNode.dir(dir.getFileName().toString(), relative(dir), children);
    }

    private TreeNode fileNode(Path file) {
        String name = file.getFileName().toString();
        return TreeNode.file(name, relative(file), extensionOf(name));
    }

    // -----------------------------------------------------------------------------------------
    // File content
    // -----------------------------------------------------------------------------------------

    /**
     * Read a file within a category. Validates that the (normalized) target is inside the content
     * root AND inside one of the category's configured paths, has an allowed extension, is a
     * regular file, and is within the size cap.
     */
    public FileContent file(String categoryId, String relativePath) {
        ContentProperties.Category category = requireCategory(categoryId);

        if (relativePath == null || relativePath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "path is required");
        }

        Path target = safeResolve(relativePath);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal path");
        }
        if (!isWithinCategory(category, target)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Path not allowed for this category");
        }
        if (!Files.isRegularFile(target)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + relativePath);
        }
        String name = target.getFileName().toString();
        if (!isAllowedFile(name)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File type not allowed");
        }
        try {
            if (Files.size(target) > MAX_FILE_BYTES) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File too large to display");
            }
            String content = Files.readString(target, StandardCharsets.UTF_8);
            String ext = extensionOf(name);
            boolean markdown = MARKDOWN_EXTENSIONS.contains(ext);
            return new FileContent(categoryId, relative(target), name, ext, markdown, content);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read file", e);
        }
    }

    // -----------------------------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------------------------

    /**
     * Resolve a root-relative path to an absolute, normalized path and ensure it stays inside the
     * content root. Returns null if the path escapes the root (defeats {@code ../} traversal).
     */
    private Path safeResolve(String relativePath) {
        Path resolved = root.resolve(relativePath).normalize();
        if (!resolved.startsWith(root)) {
            return null;
        }
        return resolved;
    }

    /** True if {@code target} lives inside at least one of the category's configured paths. */
    private boolean isWithinCategory(ContentProperties.Category category, Path target) {
        for (String rel : nonNull(category.paths())) {
            Path base = safeResolve(rel);
            if (base != null && (target.equals(base) || target.startsWith(base))) {
                return true;
            }
        }
        return false;
    }

    /** Path relative to the content root, using '/' so it's stable across OSes and URL-friendly. */
    private String relative(Path p) {
        return root.relativize(p).toString().replace('\\', '/');
    }

    private boolean isAllowedFile(String name) {
        return ALLOWED_EXTENSIONS.contains(extensionOf(name));
    }

    private static String extensionOf(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return "";
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static <T> List<T> nonNull(List<T> list) {
        return list == null ? List.of() : list;
    }
}
