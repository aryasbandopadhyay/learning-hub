package com.example.hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ============================================================================================
 * AuthProperties — typed binding of the {@code hub.auth.*} keys in application.yml.
 * ============================================================================================
 * <p>A deliberately simple, single-user-friendly auth model (NOT meant for a hostile public
 * audience):
 * <ul>
 *   <li><b>Admin</b> logs in with a hardcoded email + password and can manage the allow-list.</li>
 *   <li><b>Regular users</b> log in with just their email (no password) — but only if the admin
 *       has added that email to the allow-list.</li>
 * </ul>
 * The allow-list is persisted in an Azure Storage Table ({@code users-table}, in the same
 * storage account as progress). Admin credentials are NOT hardcoded — both the email and the
 * password are supplied via environment variables (HUB_AUTH_ADMIN_EMAIL / HUB_AUTH_ADMIN_PASSWORD),
 * backed by Azure Container App secrets in production. When either is unset, admin login is
 * disabled (guests logging in by allow-listed email are unaffected).
 *
 * @param enabled       master on/off switch for authentication.
 * @param adminEmail    the single admin account's email (case-insensitive).
 * @param adminPassword the admin password (plain — acceptable for this personal app only).
 * @param usersTable    Azure Table that stores allow-listed user emails.
 */
@ConfigurationProperties(prefix = "hub.auth")
public record AuthProperties(
        Boolean enabled,
        String adminEmail,
        String adminPassword,
        String usersTable
) {
    public AuthProperties {
        if (enabled == null) enabled = Boolean.TRUE;
        // Never hardcode admin credentials. Both email and password come from env
        // (HUB_AUTH_ADMIN_EMAIL / HUB_AUTH_ADMIN_PASSWORD; Azure Container App secrets in prod).
        // When either stays blank, admin login is disabled (see AuthService.authenticate) —
        // guests logging in by allow-listed email are unaffected.
        if (adminEmail == null) adminEmail = "";
        if (adminPassword == null) adminPassword = "";
        if (usersTable == null || usersTable.isBlank()) usersTable = "users";
    }

    /** True when an admin password has actually been configured (non-blank). */
    public boolean adminPasswordConfigured() {
        return adminPassword != null && !adminPassword.isBlank();
    }

    /** True when an admin email has actually been configured (non-blank). */
    public boolean adminEmailConfigured() {
        return adminEmail != null && !adminEmail.isBlank();
    }

    /** Normalised admin email for case-insensitive comparison ({@code ""} when unconfigured). */
    public String adminEmailNorm() {
        return adminEmail == null ? "" : adminEmail.trim().toLowerCase();
    }
}
