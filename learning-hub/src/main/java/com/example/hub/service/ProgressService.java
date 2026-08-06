package com.example.hub.service;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.example.hub.config.ProgressProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================================
 * ProgressService — persists which problems a user has completed.
 * ============================================================================================
 * <p>Backing store is an <strong>Azure Storage Table</strong> when a connection string is
 * configured ({@code hub.progress.connection-string}); otherwise an in-memory map is used so
 * the app still works locally without any cloud dependency.
 *
 * <h3>Table schema</h3>
 * <ul>
 *   <li><b>PartitionKey</b> = the user id (single-user today; wired to the Easy Auth principal
 *       header when present, else {@code "default"}). Partitioning by user keeps per-user reads
 *       to a single partition — the cheapest, fastest query shape in Table Storage.</li>
 *   <li><b>RowKey</b> = URL-safe Base64 of the problem path (paths contain '/', which is illegal
 *       in a RowKey, so we encode it). The presence of a row == "completed".</li>
 *   <li>Properties: {@code Path} (original path) and {@code Section} (category id) so a
 *       section-scoped reset can find the rows to delete.</li>
 * </ul>
 * Un-marking a problem simply deletes its row; a reset deletes every row in the partition
 * (optionally filtered by section).
 */
@Service
public class ProgressService {

    private static final Logger log = LoggerFactory.getLogger(ProgressService.class);

    private final ProgressProperties props;

    /** Non-null only when a valid connection string is configured. */
    private final TableClient table;

    /** In-memory fallback: user -> (path -> section). Used when no table is configured. */
    private final Map<String, Map<String, String>> memory = new ConcurrentHashMap<>();

    public ProgressService(ProgressProperties props) {
        this.props = props;
        TableClient client = null;
        if (Boolean.TRUE.equals(props.enabled())
                && props.connectionString() != null && !props.connectionString().isBlank()) {
            try {
                client = new TableClientBuilder()
                        .connectionString(props.connectionString())
                        .tableName(props.tableName())
                        .buildClient();
                try {
                    client.createTable(); // create on first use; ignore "already exists"
                } catch (Exception alreadyExists) {
                    /* table is already provisioned — fine */
                }
                log.info("ProgressService: using Azure Table '{}' for completion state.", props.tableName());
            } catch (Exception e) {
                log.warn("ProgressService: could not init Azure Table ({}); falling back to in-memory store.",
                        e.getMessage());
                client = null;
            }
        } else {
            log.info("ProgressService: no connection string configured — using in-memory store.");
        }
        this.table = client;
    }

    /* ---- Public API -------------------------------------------------------------------- */

    /** All completed problem paths for a user (optionally filtered to one section). */
    public Set<String> completed(String user, String section) {
        String u = norm(user);
        Set<String> out = new LinkedHashSet<>();
        if (table != null) {
            String filter = "PartitionKey eq '" + escape(u) + "'";
            if (section != null && !section.isBlank()) {
                filter += " and Section eq '" + escape(section) + "'";
            }
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                Object p = e.getProperty("Path");
                if (p != null) out.add(p.toString());
            }
        } else {
            memory.getOrDefault(u, Map.of()).forEach((path, sec) -> {
                if (section == null || section.isBlank() || section.equals(sec)) out.add(path);
            });
        }
        return out;
    }

    /** Mark a problem complete (or un-mark it) for a user. */
    public void set(String user, String path, String section, boolean completed) {
        String u = norm(user);
        if (path == null || path.isBlank()) return;
        String sec = section == null ? "" : section;
        if (table != null) {
            String rowKey = rowKey(path);
            if (completed) {
                TableEntity entity = new TableEntity(u, rowKey)
                        .addProperty("Path", path)
                        .addProperty("Section", sec);
                table.upsertEntity(entity); // insert-or-replace: idempotent
            } else {
                try {
                    table.deleteEntity(u, rowKey);
                } catch (Exception ignore) {
                    /* already absent — deleting a non-existent row is a no-op for us */
                }
            }
        } else {
            Map<String, String> m = memory.computeIfAbsent(u, k -> new ConcurrentHashMap<>());
            if (completed) m.put(path, sec);
            else m.remove(path);
        }
    }

    /** Reset progress for a user — all sections, or a single section when {@code section} is set. */
    public int reset(String user, String section) {
        String u = norm(user);
        int removed = 0;
        if (table != null) {
            String filter = "PartitionKey eq '" + escape(u) + "'";
            if (section != null && !section.isBlank()) {
                filter += " and Section eq '" + escape(section) + "'";
            }
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                try {
                    table.deleteEntity(e.getPartitionKey(), e.getRowKey());
                    removed++;
                } catch (Exception ignore) { /* concurrent delete — ignore */ }
            }
        } else {
            Map<String, String> m = memory.get(u);
            if (m != null) {
                if (section == null || section.isBlank()) {
                    removed = m.size();
                    m.clear();
                } else {
                    var it = m.entrySet().iterator();
                    while (it.hasNext()) {
                        if (section.equals(it.next().getValue())) { it.remove(); removed++; }
                    }
                }
            }
        }
        return removed;
    }

    /* ---- Helpers ----------------------------------------------------------------------- */

    private static String norm(String user) {
        return (user == null || user.isBlank()) ? "default" : user;
    }

    /** URL-safe Base64 of the path — RowKeys may not contain '/', '\\', '#' or '?'. */
    private static String rowKey(String path) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(path.getBytes(StandardCharsets.UTF_8));
    }

    /** Escape single quotes for an OData filter literal. */
    private static String escape(String s) {
        return s.replace("'", "''");
    }
}
