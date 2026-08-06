package com.example.hub.controller;

import com.example.hub.service.ProgressService;
import com.example.hub.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * ============================================================================================
 * ProgressController — REST API for per-user problem-completion progress.
 * ============================================================================================
 * <ul>
 *   <li>{@code GET  /api/progress?section=dsa} — the set of completed problem paths (optionally
 *       scoped to one section/category).</li>
 *   <li>{@code POST /api/progress} — mark/un-mark a problem
 *       ({@code {"path":"...","section":"dsa","completed":true}}).</li>
 *   <li>{@code POST /api/progress/reset} — clear progress for a section, or everything
 *       ({@code {"section":"dsa"}} or {@code {}}).</li>
 * </ul>
 * The user identity comes from the Azure Easy Auth principal header when the app is protected
 * ({@code X-MS-CLIENT-PRINCIPAL-NAME}); otherwise a single {@code "default"} user is used.
 */
@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progress;

    public ProgressController(ProgressService progress) {
        this.progress = progress;
    }

    /** Completed paths for the current user, optionally filtered by section. */
    @GetMapping
    public Map<String, Object> list(@RequestParam(value = "section", required = false) String section,
                                    HttpServletRequest req) {
        Set<String> done = progress.completed(user(req), section);
        return Map.of("completed", done, "count", done.size());
    }

    /** Toggle a single problem's completion. */
    @PostMapping
    public Map<String, Object> set(@RequestBody Map<String, Object> body, HttpServletRequest req) {
        String path = str(body.get("path"));
        String section = str(body.get("section"));
        boolean completed = Boolean.parseBoolean(str(body.getOrDefault("completed", "true")));
        progress.set(user(req), path, section, completed);
        return Map.of("ok", true, "path", path, "completed", completed);
    }

    /** Reset a whole section (or all sections when 'section' is absent/blank). */
    @PostMapping("/reset")
    public Map<String, Object> reset(@RequestBody(required = false) Map<String, Object> body,
                                     HttpServletRequest req) {
        String section = body == null ? null : str(body.get("section"));
        int removed = progress.reset(user(req), section);
        return Map.of("ok", true, "removed", removed);
    }

    /* ---- Helpers ----------------------------------------------------------------------- */

    /** Resolve the caller's identity from the login session, falling back to the Easy Auth
     *  header, then a single shared {@code "default"} user. */
    private static String user(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object email = session.getAttribute(AuthService.SESSION_EMAIL);
            if (email != null && !email.toString().isBlank()) return email.toString();
        }
        String principal = req.getHeader("X-MS-CLIENT-PRINCIPAL-NAME");
        return (principal == null || principal.isBlank()) ? "default" : principal;
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }
}
