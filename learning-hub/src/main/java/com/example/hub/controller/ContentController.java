package com.example.hub.controller;

import com.example.hub.model.CategoryDto;
import com.example.hub.model.FileContent;
import com.example.hub.model.TreeNode;
import com.example.hub.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ============================================================================================
 * ContentController — the JSON REST API consumed by the frontend (static/app.js).
 * ============================================================================================
 * {@code @RestController} = {@code @Controller} + {@code @ResponseBody}: every handler's return
 * value is serialized to JSON by Jackson (auto-configured by Spring Boot). {@code @RequestMapping}
 * at the class level prefixes all routes with {@code /api}.
 *
 * Endpoints:
 * <ul>
 *   <li>{@code GET /api/categories}          — the tabs (id/label/description).</li>
 *   <li>{@code GET /api/tree/{category}}      — the file tree for a category.</li>
 *   <li>{@code GET /api/file?category=&path=} — a single file's content.</li>
 * </ul>
 *
 * The controller is intentionally thin: all logic (walking, security, reading) lives in
 * {@link ContentService}, which is injected via the constructor (Dependency Injection).
 */
@RestController
@RequestMapping("/api")
public class ContentController {

    private final ContentService content;

    public ContentController(ContentService content) {
        this.content = content;
    }

    /** The list of subject areas; the UI renders one tab per entry (so new categories auto-appear). */
    @GetMapping("/categories")
    public List<CategoryDto> categories() {
        return content.categories();
    }

    /** The (nested) file tree for one category. {@code @PathVariable} binds the {id} URL segment. */
    @GetMapping("/tree/{category}")
    public TreeNode tree(@PathVariable("category") String category) {
        return content.tree(category);
    }

    /**
     * The content of a single file. {@code @RequestParam} binds query-string params
     * ({@code ?category=lld&path=parking-lot/java/README.md}).
     */
    @GetMapping("/file")
    public FileContent file(@RequestParam("category") String category,
                            @RequestParam("path") String path) {
        return content.file(category, path);
    }
}
