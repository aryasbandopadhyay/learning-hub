package com.example.hub.controller;

import com.example.hub.service.AuthService;
import com.example.hub.service.NotesService;
import com.example.hub.service.ProgressService;
import com.example.hub.service.ReviewService;
import com.example.hub.service.SolutionsService;
import com.example.hub.service.StatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * DataController — portable JSON export/import for all user-owned learning state.
 * ============================================================================================
 * <p>Exports contain no Azure keys or internal row identifiers. Import is a tolerant merge:
 * missing arrays are ignored and each supplied row is upserted into the authenticated user's
 * partition, so a backup may safely be restored on another device.
 */
@RestController
@RequestMapping("/api/data")
public class DataController {

    private final ProgressService progress;
    private final NotesService notes;
    private final ReviewService reviews;
    private final StatsService stats;
    private final SolutionsService solutions;

    public DataController(ProgressService progress, NotesService notes, ReviewService reviews,
                          StatsService stats, SolutionsService solutions) {
        this.progress = progress;
        this.notes = notes;
        this.reviews = reviews;
        this.stats = stats;
        this.solutions = solutions;
    }

    /** Export the complete current-user document in version-1 format. */
    @GetMapping("/export")
    public Map<String, Object> export(HttpServletRequest req) {
        String u = user(req);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("version", 1);
        out.put("exportedAt", OffsetDateTime.now(ZoneOffset.UTC).toString());
        out.put("user", u);
        out.put("progress", progress.entries(u));
        out.put("notes", notes.all(u));
        out.put("reviews", reviews.all(u));
        out.put("stats", stats.all(u));
        out.put("solutions", solutions.allCurrent(u));
        return out;
    }

    /** Merge a version-1-style export document into the current user's stores. */
    @PostMapping("/import")
    public Map<String, Object> importData(@RequestBody(required = false) Map<String, Object> body,
                                          HttpServletRequest req) {
        String u = user(req);
        Map<String, Object> source = body == null ? Map.of() : body;
        int progressCount = 0;
        int notesCount = 0;
        int reviewCount = 0;
        int statsCount = 0;
        int solutionCount = 0;

        for (Map<String, Object> row : rows(source.get("progress"))) {
            String path = str(row.get("path"));
            if (path == null || path.isBlank()) continue;
            progress.set(u, path, str(row.get("section")), true);
            progressCount++;
        }
        for (Map<String, Object> row : rows(source.get("notes"))) {
            String path = str(row.get("path"));
            if (path == null || path.isBlank()) continue;
            notes.save(u, path, str(row.get("text")));
            notesCount++;
        }
        for (Map<String, Object> row : rows(source.get("reviews"))) {
            String path = str(row.get("path"));
            if (path == null || path.isBlank()) continue;
            reviews.importRow(u, row);
            reviewCount++;
        }
        for (Map<String, Object> row : rows(source.get("stats"))) {
            String path = str(row.get("path"));
            if (path == null || path.isBlank()) continue;
            stats.importRow(u, row);
            statsCount++;
        }
        for (Map<String, Object> row : rows(source.get("solutions"))) {
            String path = str(row.get("path"));
            String code = str(row.get("code"));
            if (path == null || path.isBlank() || code == null) continue;
            solutions.save(u, path, section(path), code, str(row.get("language")));
            solutionCount++;
        }

        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("progress", progressCount);
        counts.put("notes", notesCount);
        counts.put("reviews", reviewCount);
        counts.put("stats", statsCount);
        counts.put("solutions", solutionCount);
        return Map.of("ok", true, "imported", counts);
    }

    /** Convert a loosely bound JSON array into map rows and silently ignore malformed entries. */
    private static List<Map<String, Object>> rows(Object value) {
        if (!(value instanceof List<?> list)) return List.of();
        return list.stream()
                .filter(Map.class::isInstance)
                .map(item -> {
                    Map<?, ?> raw = (Map<?, ?>) item;
                    Map<String, Object> row = new LinkedHashMap<>();
                    raw.forEach((key, val) -> row.put(String.valueOf(key), val));
                    return row;
                })
                .toList();
    }

    private static String user(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object email = session.getAttribute(AuthService.SESSION_EMAIL);
            if (email != null && !email.toString().isBlank()) return email.toString();
        }
        String principal = req.getHeader("X-MS-CLIENT-PRINCIPAL-NAME");
        return principal == null || principal.isBlank() ? "default" : principal;
    }

    private static String section(String path) {
        String norm = path.replace('\\', '/');
        if (norm.startsWith("dsa/google/")) return "google";
        if (norm.startsWith("dsa/faang/")) return "faang";
        int slash = norm.indexOf('/');
        return slash < 0 ? norm : norm.substring(0, slash);
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }
}
