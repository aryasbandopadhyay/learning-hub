package com.example.hub;

import com.example.hub.config.AuthProperties;
import com.example.hub.config.ProgressProperties;
import com.example.hub.service.AuthService;
import com.example.hub.service.UserService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AuthService} against the in-memory allow-list (no Azure connection string),
 * covering the two-tier model: password-checked admin + password-less allow-listed guests.
 */
class AuthServiceTest {

    /** Wire AuthService + a UserService backed by the in-memory allow-list. */
    private AuthService authWith(UserService users) {
        AuthProperties auth = new AuthProperties(true, "admin@example.com", "s3cret", "users",
                "local-dev-only-change-me");
        return new AuthService(auth, users);
    }

    private UserService inMemoryUsers() {
        // Blank connection string => in-memory allow-list.
        return new UserService(
                new AuthProperties(true, "admin@example.com", "s3cret", "users",
                        "local-dev-only-change-me"),
                new ProgressProperties(true, "", "progress"));
    }

    @Test
    void adminAuthenticatesWithCorrectPassword() {
        AuthService svc = authWith(inMemoryUsers());
        assertThat(svc.authenticate("admin@example.com", "s3cret")).isEqualTo(AuthService.ROLE_ADMIN);
    }

    @Test
    void adminIsRejectedWithWrongOrMissingPassword() {
        AuthService svc = authWith(inMemoryUsers());
        assertThat(svc.authenticate("admin@example.com", "nope")).isNull();
        assertThat(svc.authenticate("admin@example.com", null)).isNull();
    }

    @Test
    void adminEmailIsCaseInsensitive() {
        AuthService svc = authWith(inMemoryUsers());
        assertThat(svc.authenticate("  ADMIN@Example.com ", "s3cret")).isEqualTo(AuthService.ROLE_ADMIN);
        assertThat(svc.isAdminEmail("Admin@example.COM")).isTrue();
        assertThat(svc.isAdminEmail("someone@example.com")).isFalse();
    }

    @Test
    void allowListedGuestAuthenticatesWithoutPassword() {
        UserService users = inMemoryUsers();
        users.add("guest@example.com", "admin@example.com");
        AuthService svc = authWith(users);

        // Password is ignored for guests — any value (incl. null) is accepted once allow-listed.
        assertThat(svc.authenticate("guest@example.com", null)).isEqualTo(AuthService.ROLE_USER);
        assertThat(svc.authenticate("GUEST@example.com", "whatever")).isEqualTo(AuthService.ROLE_USER);
    }

    @Test
    void unknownEmailIsDenied() {
        AuthService svc = authWith(inMemoryUsers());
        assertThat(svc.authenticate("stranger@example.com", "")).isNull();
        assertThat(svc.authenticate(null, "x")).isNull();
        assertThat(svc.authenticate("  ", "x")).isNull();
    }

    @Test
    void adminIsDisabledWhenNoPasswordConfigured() {
        // Simulates HUB_AUTH_ADMIN_PASSWORD being unset (blank) — the redacted default.
        AuthProperties noPw = new AuthProperties(true, "admin@example.com", "", "users",
                "local-dev-only-change-me");
        AuthService svc = new AuthService(noPw, inMemoryUsers());
        assertThat(noPw.adminPasswordConfigured()).isFalse();
        // No password (blank or otherwise) may grant admin when none is configured.
        assertThat(svc.authenticate("admin@example.com", "")).isNull();
        assertThat(svc.authenticate("admin@example.com", null)).isNull();
        assertThat(svc.authenticate("admin@example.com", "anything")).isNull();
    }

    @Test
    void adminIsDisabledWhenNoEmailConfigured() {
        // Simulates HUB_AUTH_ADMIN_EMAIL being unset (blank) — the redacted default.
        AuthProperties noEmail = new AuthProperties(true, "", "s3cret", "users",
                "local-dev-only-change-me");
        AuthService svc = new AuthService(noEmail, inMemoryUsers());
        assertThat(noEmail.adminEmailConfigured()).isFalse();
        // A blank submitted email is always rejected, and no email may match an unconfigured admin.
        assertThat(svc.authenticate("", "s3cret")).isNull();
        assertThat(svc.authenticate("  ", "s3cret")).isNull();
        assertThat(svc.isAdminEmail("")).isFalse();
    }

    @Test
    void removingAGuestRevokesAccess() {
        UserService users = inMemoryUsers();
        users.add("temp@example.com", "admin@example.com");
        AuthService svc = authWith(users);
        assertThat(svc.authenticate("temp@example.com", null)).isEqualTo(AuthService.ROLE_USER);

        users.remove("temp@example.com");
        assertThat(svc.authenticate("temp@example.com", null)).isNull();
    }
}
