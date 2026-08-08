package com.example.hub.controller;

import com.example.hub.service.AuthService;
import com.example.hub.service.StatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ============================================================================================
 * StatsController — REST API for attempts, solve activity and complexity self-reports.
 * ============================================================================================
 * The controller only performs JSON binding and user resolution; persistence and UTC streak
 * calculations live in {@link StatsService}.
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService stats;

    public StatsController(StatsService stats) {
        this.stats = stats;
    }

    /** Increment an attempt and optionally mark/update a successful solve. */
    @PostMapping("/attempt")
    public Map<String, Object> attempt(@RequestBody Map<String, Object> body,
                                       HttpServletRequest req) {
        return stats.attempt(user(req), str(body.get("path")), str(body.get("section")),
                bool(body.get("solved")), number(body.get("elapsedMs"), 0L));
    }

    /** Store the learner's claimed and empirically measured complexity labels. */
    @PostMapping("/selfreport")
    public Map<String, Object> selfReport(@RequestBody Map<String, Object> body,
                                          HttpServletRequest req) {
        stats.selfReport(user(req), str(body.get("path")), str(body.get("self")),
                str(body.get("measured")));
        return Map.of("ok", true);
    }

    /** Fetch one row, returning explicit defaults when the problem has no activity yet. */
    @GetMapping
    public Map<String, Object> get(@RequestParam("path") String path, HttpServletRequest req) {
        return stats.get(user(req), path).orElseGet(() -> empty(path));
    }

    /** UTC solve calendar plus current/longest streaks. */
    @GetMapping("/activity")
    public Map<String, Object> activity(HttpServletRequest req) {
        return stats.activity(user(req));
    }

    /** All statistics rows for dashboards and export. */
    @GetMapping("/all")
    public Map<String, Object> all(HttpServletRequest req) {
        return Map.of("items", stats.all(user(req)));
    }

    private static Map<String, Object> empty(String path) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("path", path);
        out.put("section", "");
        out.put("attempts", 0);
        out.put("solved", false);
        out.put("firstSolvedAt", "");
        out.put("lastAttemptAt", "");
        out.put("bestTimeMs", 0L);
        out.put("selfComplexity", "");
        out.put("measuredComplexity", "");
        return out;
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

    private static boolean bool(Object value) {
        return value instanceof Boolean b ? b
                : Boolean.parseBoolean(value == null ? "false" : value.toString());
    }

    private static long number(Object value, long fallback) {
        if (value instanceof Number n) return n.longValue();
        try {
            return value == null ? fallback : Long.parseLong(value.toString());
        } catch (NumberFormatException invalid) {
            return fallback;
        }
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }
}
