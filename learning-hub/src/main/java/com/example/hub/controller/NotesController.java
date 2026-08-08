package com.example.hub.controller;

import com.example.hub.service.AuthService;
import com.example.hub.service.NotesService;
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
 * NotesController — REST API for per-problem study notes.
 * ============================================================================================
 * <ul>
 *   <li>{@code GET /api/notes?path=...} returns the current note or empty strings.</li>
 *   <li>{@code POST /api/notes} with {@code {path,text}} saves it; blank text deletes it.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/notes")
public class NotesController {

    private final NotesService notes;

    public NotesController(NotesService notes) {
        this.notes = notes;
    }

    /** Fetch a note while keeping a stable response shape when none has been written yet. */
    @GetMapping
    public Map<String, Object> get(@RequestParam("path") String path, HttpServletRequest req) {
        return notes.get(user(req), path).orElseGet(() -> {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("path", path);
            empty.put("text", "");
            empty.put("updatedAt", "");
            return empty;
        });
    }

    /** Save/update/delete a note. */
    @PostMapping
    public Map<String, Object> save(@RequestBody Map<String, Object> body, HttpServletRequest req) {
        notes.save(user(req), str(body.get("path")), str(body.get("text")));
        return Map.of("ok", true);
    }

    /** Resolve the authenticated caller from session, Easy Auth header, then local default. */
    private static String user(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object email = session.getAttribute(AuthService.SESSION_EMAIL);
            if (email != null && !email.toString().isBlank()) return email.toString();
        }
        String principal = req.getHeader("X-MS-CLIENT-PRINCIPAL-NAME");
        return principal == null || principal.isBlank() ? "default" : principal;
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }
}
