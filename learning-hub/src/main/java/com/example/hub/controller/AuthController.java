package com.example.hub.controller;

import com.example.hub.service.AuthService;
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
 * AuthController — login / logout / whoami for the simple session auth.
 * ============================================================================================
 * <ul>
 *   <li>{@code POST /api/auth/login} — body {@code {"email":"...","password":"..."}}. Admin needs
 *       the password; allow-listed users need only the email. On success the identity is stored
 *       in the HTTP session.</li>
 *   <li>{@code POST /api/auth/logout} — invalidate the session.</li>
 *   <li>{@code GET  /api/auth/me} — current identity ({@code {authenticated, email, role, admin}}).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpServletRequest req) {
        String email = body == null ? null : body.get("email");
        String password = body == null ? null : body.get("password");
        String role = auth.authenticate(email, password);
        if (role == null) {
            // Distinguish "not allow-listed" from "bad admin password" without leaking which.
            return Map.of("authenticated", false,
                    "error", "Access denied. Ask the admin to add your email, or check the admin password.");
        }
        String norm = email.trim().toLowerCase();
        HttpSession session = req.getSession(true); // create a fresh session
        session.setAttribute(AuthService.SESSION_EMAIL, norm);
        session.setAttribute(AuthService.SESSION_ROLE, role);
        return Map.of("authenticated", true, "email", norm, "role", role,
                "admin", AuthService.ROLE_ADMIN.equals(role));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        return Map.of("ok", true);
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute(AuthService.SESSION_EMAIL);
        String role = session == null ? null : (String) session.getAttribute(AuthService.SESSION_ROLE);
        if (email == null) {
            return Map.of("authenticated", false);
        }
        return Map.of("authenticated", true, "email", email, "role", role,
                "admin", AuthService.ROLE_ADMIN.equals(role));
    }
}
