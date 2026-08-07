package com.example.hub.web;

import com.example.hub.config.AuthProperties;
import com.example.hub.service.AuthCookieService;
import com.example.hub.service.AuthService;
import jakarta.servlet.http.Cookie;
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
    private final AuthCookieService cookies;

    public AuthFilter(AuthService auth, AuthCookieService cookies, AuthProperties props) {
        this.auth = auth;
        this.cookies = cookies;
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

        HttpSession session = rehydrateSessionFromCookieIfNeeded(req);
        String email = session == null ? null : (String) session.getAttribute(AuthService.SESSION_EMAIL);
        String role = session == null ? null : (String) session.getAttribute(AuthService.SESSION_ROLE);

        if (PUBLIC_PATHS.contains(path)) {
            chain.doFilter(req, res);
            return;
        }

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

    /**
     * If the in-memory servlet session is gone (browser restart/redeploy), verify the signed
     * remember-me cookie and recreate the session attributes downstream controllers already use.
     */
    private HttpSession rehydrateSessionFromCookieIfNeeded(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(AuthService.SESSION_EMAIL) != null) return session;

        String token = null;
        Cookie[] requestCookies = req.getCookies();
        if (requestCookies != null) {
            for (Cookie cookie : requestCookies) {
                if (AuthCookieService.COOKIE_NAME.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) return session;

        return cookies.verify(token).map(authToken -> {
            HttpSession fresh = req.getSession(true);
            fresh.setAttribute(AuthService.SESSION_EMAIL, authToken.email());
            fresh.setAttribute(AuthService.SESSION_ROLE, authToken.role());
            return fresh;
        }).orElse(session);
    }
}
