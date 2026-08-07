package com.example.hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ============================================================================================
 * ProgressProperties — typed binding of the {@code hub.progress.*} keys in application.yml.
 * ============================================================================================
 * <p>Tracks which DSA problems the user has completed so a per-section progress bar can be
 * rendered. State is persisted in an <em>Azure Storage Table</em> (very cheap, and it syncs
 * across devices). If no {@code connection-string} is supplied the {@code ProgressService}
 * degrades gracefully to an in-memory store (handy for local development).
 *
 * <pre>
 *   hub:
 *     progress:
 *       enabled: true
 *       connection-string: ""      # set via env HUB_PROGRESS_CONNECTION_STRING in the cloud
 *       table-name: progress
 *       solutions-table: solutions # saved accepted submissions, using the same storage account
 * </pre>
 *
 * @param enabled          master on/off switch for the progress feature.
 * @param connectionString Azure Storage account connection string. Blank => in-memory fallback.
 * @param tableName        the table that holds completion rows (created on first use).
 * @param solutionsTable   the table that holds per-user saved passing solutions.
 */
@ConfigurationProperties(prefix = "hub.progress")
public record ProgressProperties(
        Boolean enabled,
        String connectionString,
        String tableName,
        String solutionsTable
) {
    /** Fill in safe defaults for any key omitted from application.yml / the environment. */
    public ProgressProperties {
        if (enabled == null) enabled = Boolean.TRUE;
        if (connectionString == null) connectionString = "";
        if (tableName == null || tableName.isBlank()) tableName = "progress";
        if (solutionsTable == null || solutionsTable.isBlank()) solutionsTable = "solutions";
    }
}
