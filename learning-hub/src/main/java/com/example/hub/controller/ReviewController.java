package com.example.hub.controller;

import com.example.hub.service.AuthService;
import com.example.hub.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ============================================================================================
 * ReviewController — REST API for SM-2 spaced-repetition schedules.
 * ============================================================================================
 * Grades update one schedule, due/all expose review queues, and reset clears the current user's
 * review partition. Authentication is inherited from the existing {@code /api/**} filter.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviews;

    public ReviewController(ReviewService reviews) {
        this.reviews = reviews;
    }

    /** Apply a quality grade in the SM-2 range 0..5. */
    @PostMapping("/grade")
    public Map<String, Object> grade(@RequestBody Map<String, Object> body,
                                     HttpServletRequest req) {
        return reviews.grade(user(req), str(body.get("path")), str(body.get("section")),
                integer(body.get("quality"), 0));
    }

    /** Review rows due today or earlier. */
    @GetMapping("/due")
    public Map<String, Object> due(HttpServletRequest req) {
        var due = reviews.due(user(req));
        return Map.of("due", due, "count", due.size());
    }

    /** Every review row for export/history views. */
    @GetMapping("/all")
    public Map<String, Object> all(HttpServletRequest req) {
        return Map.of("items", reviews.all(user(req)));
    }

    /** Clear every spaced-repetition row for the caller. */
    @PostMapping("/reset")
    public Map<String, Object> reset(HttpServletRequest req) {
        return Map.of("ok", true, "removed", reviews.reset(user(req)));
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

    private static int integer(Object value, int fallback) {
        if (value instanceof Number n) return n.intValue();
        try {
            return value == null ? fallback : Integer.parseInt(value.toString());
        } catch (NumberFormatException invalid) {
            return fallback;
        }
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }
}
