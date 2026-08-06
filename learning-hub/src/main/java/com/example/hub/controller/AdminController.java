package com.example.hub.controller;

import com.example.hub.service.AuthService;
import com.example.hub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * AdminController — manage the email allow-list (admin only).
 * ============================================================================================
 * All routes live under {@code /api/admin/**}; the {@link com.example.hub.web.AuthFilter} enforces
 * that only an {@code admin} session may reach them.
 * <ul>
 *   <li>{@code GET    /api/admin/users} — list allow-listed users.</li>
 *   <li>{@code POST   /api/admin/users} — add an email ({@code {"email":"..."}}).</li>
 *   <li>{@code DELETE /api/admin/users?email=...} — remove an email.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService users;

    public AdminController(UserService users) {
        this.users = users;
    }

    @GetMapping("/users")
    public Map<String, Object> list() {
        List<Map<String, Object>> all = users.list();
        return Map.of("users", all, "count", all.size());
    }

    @PostMapping("/users")
    public Map<String, Object> add(@RequestBody Map<String, String> body, HttpServletRequest req) {
        String email = body == null ? null : body.get("email");
        if (email == null || email.isBlank() || !email.contains("@")) {
            return Map.of("ok", false, "error", "Enter a valid email address.");
        }
        String addedBy = currentEmail(req);
        String norm = users.add(email, addedBy);
        return Map.of("ok", true, "email", norm);
    }

    @DeleteMapping("/users")
    public Map<String, Object> remove(@RequestParam("email") String email) {
        users.remove(email);
        return Map.of("ok", true, "email", email == null ? "" : email.trim().toLowerCase());
    }

    private static String currentEmail(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? "" : (String) s.getAttribute(AuthService.SESSION_EMAIL);
    }
}
