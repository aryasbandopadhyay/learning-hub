package com.example.hub.service;

import com.example.hub.config.AuthProperties;
import org.springframework.stereotype.Service;

/**
 * ============================================================================================
 * AuthService — validates logins for the simple allow-list auth model.
 * ============================================================================================
 * <ul>
 *   <li><b>Admin</b>: email == configured admin email AND password matches.</li>
 *   <li><b>User</b>: email is on the allow-list (password ignored).</li>
 * </ul>
 * Roles and session-attribute keys are defined here so controllers and the servlet filter agree.
 */
@Service
public class AuthService {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    public static final String SESSION_EMAIL = "hub.email";
    public static final String SESSION_ROLE = "hub.role";

    private final AuthProperties props;
    private final UserService users;

    public AuthService(AuthProperties props, UserService users) {
        this.props = props;
        this.users = users;
    }

    public boolean enabled() {
        return Boolean.TRUE.equals(props.enabled());
    }

    /**
     * Validate a login attempt.
     *
     * @return the granted role ({@link #ROLE_ADMIN}/{@link #ROLE_USER}), or {@code null} if denied.
     */
    public String authenticate(String email, String password) {
        if (email == null || email.isBlank()) return null;
        String norm = email.trim().toLowerCase();
        if (props.adminEmailConfigured() && norm.equals(props.adminEmailNorm())) {
            // Admin requires a configured (non-blank) email AND password AND an exact password
            // match. If either credential is unconfigured (HUB_AUTH_ADMIN_EMAIL /
            // HUB_AUTH_ADMIN_PASSWORD unset), admin login is disabled entirely.
            return (props.adminPasswordConfigured()
                    && password != null && password.equals(props.adminPassword()))
                    ? ROLE_ADMIN : null;
        }
        return users.isAllowed(norm) ? ROLE_USER : null;
    }

    public boolean isAdminEmail(String email) {
        return props.adminEmailConfigured()
                && email != null && email.trim().toLowerCase().equals(props.adminEmailNorm());
    }
}
