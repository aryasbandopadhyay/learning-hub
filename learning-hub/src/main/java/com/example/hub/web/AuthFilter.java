package com.example.hub.web;

import com.example.hub.config.AuthProperties;
import com.example.hub.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * ============================================================================================
 * AuthFilter — gates the entire app behind the simple session login.
 * ============================================================================================
 * <p>Unauthenticated requests are either redirected to the login page (for browser navigations)
 * or answered with 401 (for API calls). Admin-only endpoints ({@code /api/admin/**}) additionally
 * require the {@code admin} role. A small set of paths is always public so the login page can load
 * and the login/identity endpoints can be reached.
 *
 * <p>Registered as a {@code @Component} extending {@link OncePerRequestFilter}, so Spring Boot
 * auto-registers it for every request.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    /** Paths reachable without a session. */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login.html",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/me",
            "/favicon.ico",
            "/error"
    );

    private final AuthService auth;

    public AuthFilter(AuthService auth, AuthProperties props) {
        this.auth = auth;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // Auth disabled => let everything through (useful for local/dev toggling).
        if (!auth.enabled()) {
            chain.doFilter(req, res);
            return;
        }

        String path = req.getRequestURI();
        if (req.getContextPath() != null && !req.getContextPath().isEmpty() && path.startsWith(req.getContextPath())) {
            path = path.substring(req.getContextPath().length());
        }

        if (PUBLIC_PATHS.contains(path)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute(AuthService.SESSION_EMAIL);
        String role = session == null ? null : (String) session.getAttribute(AuthService.SESSION_ROLE);

        boolean isApi = path.startsWith("/api/");

        if (email == null) {
            if (isApi) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"authenticated\":false,\"error\":\"login required\"}");
            } else {
                res.sendRedirect("/login.html");
            }
            return;
        }

        // Admin-only area.
        if (path.startsWith("/api/admin/") && !AuthService.ROLE_ADMIN.equals(role)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"admin only\"}");
            return;
        }

        chain.doFilter(req, res);
    }
}
