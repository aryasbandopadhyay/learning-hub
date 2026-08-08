package com.example.hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

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
 *       notes-table: notes          # per-problem study notes
 *       reviews-table: reviews      # SM-2 spaced-repetition state
 *       stats-table: stats          # attempts, solves, timing and complexity reports
 * </pre>
 *
 * @param enabled          master on/off switch for the progress feature.
 * @param connectionString Azure Storage account connection string. Blank => in-memory fallback.
 * @param tableName        the table that holds completion rows (created on first use).
 * @param solutionsTable   the table that holds per-user saved passing solutions.
 * @param notesTable       the table that holds per-user problem notes.
 * @param reviewsTable     the table that holds spaced-repetition review rows.
 * @param statsTable       the table that holds per-problem activity/statistics rows.
 */
@ConfigurationProperties(prefix = "hub.progress")
public record ProgressProperties(
        Boolean enabled,
        String connectionString,
        String tableName,
        String solutionsTable,
        String notesTable,
        String reviewsTable,
        String statsTable
) {
    /** Backward-compatible constructor used by the existing focused unit tests. */
    public ProgressProperties(Boolean enabled, String connectionString, String tableName,
                              String solutionsTable) {
        this(enabled, connectionString, tableName, solutionsTable, "notes", "reviews", "stats");
    }

    /** Fill in safe defaults for any key omitted from application.yml / the environment.
     *  {@code @ConstructorBinding} marks THIS (the canonical) constructor as the one Spring
     *  uses for property binding — required because the record now also declares a second
     *  (4-arg convenience) constructor, which would otherwise make binding ambiguous. */
    @ConstructorBinding
    public ProgressProperties {
        if (enabled == null) enabled = Boolean.TRUE;
        if (connectionString == null) connectionString = "";
        if (tableName == null || tableName.isBlank()) tableName = "progress";
        if (solutionsTable == null || solutionsTable.isBlank()) solutionsTable = "solutions";
        if (notesTable == null || notesTable.isBlank()) notesTable = "notes";
        if (reviewsTable == null || reviewsTable.isBlank()) reviewsTable = "reviews";
        if (statsTable == null || statsTable.isBlank()) statsTable = "stats";
    }
}
