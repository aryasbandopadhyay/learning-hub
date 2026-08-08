package com.example.hub.service;

import com.azure.core.exception.HttpResponseException;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.example.hub.config.ProgressProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ============================================================================================
 * SolutionsService — persists the code a user last submitted successfully for a problem.
 * ============================================================================================
 * <p>This mirrors {@link ProgressService}: when {@code hub.progress.connection-string} is set,
 * rows are written to Azure Table Storage; otherwise we keep an in-memory map so local
 * development and offline demos keep working without Azure.
 *
 * <h3>Table schema</h3>
 * <ul>
 *   <li><b>PartitionKey</b> = normalized user id / email.</li>
 *   <li><b>RowKey</b> = URL-safe Base64 of the problem path for the current row; immutable
 *       history rows append {@code |<13-digit epoch millis>}.</li>
 *   <li>Properties: {@code Path}, {@code Section}, {@code Code}, {@code Language},
 *       {@code UpdatedAt}. One row is the user's current saved solution for that problem.</li>
 * </ul>
 *
 * <p>Azure Table entities have size limits (string properties are small and the whole entity is
 * capped at 1 MB), so we intentionally skip unusually large submissions instead of failing a
 * successful judge run. The app's Python submissions are expected to be tiny.
 */
@Service
public class SolutionsService {

    private static final Logger log = LoggerFactory.getLogger(SolutionsService.class);

    /** Leave room for UTF-8 expansion and other entity properties under Azure Table limits. */
    private static final int MAX_CODE_BYTES = 60_000;

    /** Non-null only when a valid connection string is configured. */
    private final TableClient table;

    /** In-memory fallback: user -> (path -> saved solution row). */
    private final Map<String, Map<String, SavedSolution>> memory = new ConcurrentHashMap<>();

    /** In-memory history: user -> (path -> oldest-to-newest accepted submissions). */
    private final Map<String, Map<String, List<HistoryEntry>>> historyMemory = new ConcurrentHashMap<>();

    public SolutionsService(ProgressProperties props) {
        TableClient client = null;
        if (Boolean.TRUE.equals(props.enabled())
                && props.connectionString() != null && !props.connectionString().isBlank()) {
            try {
                client = new TableClientBuilder()
                        .connectionString(props.connectionString())
                        .tableName(props.solutionsTable())
                        .buildClient();
                try {
                    client.createTable(); // create on first use; ignore "already exists"
                } catch (Exception alreadyExists) {
                    /* table is already provisioned — fine */
                }
                log.info("SolutionsService: using Azure Table '{}' for saved solutions.",
                        props.solutionsTable());
            } catch (Exception e) {
                log.warn("SolutionsService: could not init Azure Table ({}); falling back to in-memory store.",
                        e.getMessage());
                client = null;
            }
        } else {
            log.info("SolutionsService: no connection string configured — using in-memory store.");
        }
        this.table = client;
    }

    /* ---- Public API -------------------------------------------------------------------- */

    /** Save (insert-or-replace) the latest all-tests-passing solution for a user/problem. */
    public void save(String user, String path, String section, String code, String language) {
        String u = norm(user);
        if (path == null || path.isBlank() || code == null) return;
        if (code.getBytes(StandardCharsets.UTF_8).length > MAX_CODE_BYTES) {
            log.warn("SolutionsService: skipping oversized saved solution for path '{}'.", path);
            return;
        }

        String sec = section == null ? "" : section;
        String lang = (language == null || language.isBlank()) ? "python" : language;
        String updatedAt = OffsetDateTime.now(ZoneOffset.UTC).toString();

        if (table != null) {
            String currentKey = rowKey(path);
            TableEntity entity = new TableEntity(u, rowKey(path))
                    .addProperty("Path", path)
                    .addProperty("Section", sec)
                    .addProperty("Code", code)
                    .addProperty("Language", lang)
                    .addProperty("UpdatedAt", updatedAt);
            table.upsertEntity(entity); // insert-or-replace: one current solution per problem
            TableEntity history = new TableEntity(u,
                    currentKey + "|" + String.format("%013d", System.currentTimeMillis()))
                    .addProperty("Path", path)
                    .addProperty("Section", sec)
                    .addProperty("Code", code)
                    .addProperty("Language", lang)
                    .addProperty("UpdatedAt", updatedAt)
                    .addProperty("Passed", true);
            table.upsertEntity(history);
        } else {
            memory.computeIfAbsent(u, k -> new ConcurrentHashMap<>())
                    .put(path, new SavedSolution(path, sec, code, lang, updatedAt));
            historyMemory.computeIfAbsent(u, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(path, k -> new CopyOnWriteArrayList<>())
                    .add(new HistoryEntry(code, lang, updatedAt, true));
        }
    }

    /** Return the current saved solution for a user/problem, if one exists. */
    public Optional<Map<String, Object>> get(String user, String path) {
        String u = norm(user);
        if (path == null || path.isBlank()) return Optional.empty();

        if (table != null) {
            try {
                TableEntity e = table.getEntity(u, rowKey(path));
                return Optional.of(toMap(
                        prop(e, "Code"),
                        prop(e, "UpdatedAt"),
                        prop(e, "Language"),
                        prop(e, "Section"),
                        prop(e, "Path")));
            } catch (HttpResponseException missing) {
                // A missing row — or a not-yet-created table — surfaces as a 404
                // (ResourceNotFoundException or TableServiceException). Treat both as "no saved solution".
                if (missing.getResponse() != null && missing.getResponse().getStatusCode() == 404) {
                    return Optional.empty();
                }
                throw missing;
            }
        }

        SavedSolution s = memory.getOrDefault(u, Map.of()).get(path);
        if (s == null) return Optional.empty();
        return Optional.of(toMap(s.code(), s.updatedAt(), s.language(), s.section(), s.path()));
    }

    /** All problem paths with saved solutions for a user. Useful for future dashboard badges. */
    public Set<String> solvedPaths(String user) {
        String u = norm(user);
        Set<String> out = new LinkedHashSet<>();
        if (table != null) {
            String filter = "PartitionKey eq '" + escape(u) + "'";
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                Object p = e.getProperty("Path");
                if (p != null && e.getRowKey().equals(rowKey(p.toString()))) out.add(p.toString());
            }
        } else {
            out.addAll(memory.getOrDefault(u, Map.of()).keySet());
        }
        return out;
    }

    /** Newest accepted submissions for one problem, excluding the mutable current row. */
    public List<Map<String, Object>> history(String user, String path, int limit) {
        String u = norm(user);
        if (path == null || path.isBlank() || limit <= 0) return List.of();
        int capped = Math.max(0, limit);
        List<Map<String, Object>> out = new ArrayList<>();
        if (table != null) {
            String prefix = rowKey(path) + "|";
            String filter = "PartitionKey eq '" + escape(u) + "' and Path eq '" + escape(path) + "'";
            List<TableEntity> rows = new ArrayList<>();
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                if (e.getRowKey().startsWith(prefix)) rows.add(e);
            }
            rows.sort(Comparator.comparing(TableEntity::getRowKey).reversed());
            for (TableEntity e : rows) {
                if (out.size() >= capped) break;
                out.add(historyMap(prop(e, "Code"), prop(e, "Language"),
                        prop(e, "UpdatedAt"), boolProp(e, "Passed")));
            }
        } else {
            List<HistoryEntry> rows = historyMemory.getOrDefault(u, Map.of())
                    .getOrDefault(path, List.of());
            for (int i = rows.size() - 1; i >= 0 && out.size() < capped; i--) {
                HistoryEntry h = rows.get(i);
                out.add(historyMap(h.code(), h.language(), h.updatedAt(), h.passed()));
            }
        }
        return out;
    }

    /** Export all current saved solutions; history rows are intentionally excluded. */
    public List<Map<String, Object>> allCurrent(String user) {
        String u = norm(user);
        List<Map<String, Object>> out = new ArrayList<>();
        if (table != null) {
            String filter = "PartitionKey eq '" + escape(u) + "'";
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                String path = prop(e, "Path");
                if (path.isBlank() || !e.getRowKey().equals(rowKey(path))) continue;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("path", path);
                row.put("code", prop(e, "Code"));
                row.put("language", language(prop(e, "Language")));
                out.add(row);
            }
        } else {
            memory.getOrDefault(u, Map.of()).values().forEach(s -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("path", s.path());
                row.put("code", s.code());
                row.put("language", language(s.language()));
                out.add(row);
            });
        }
        return out;
    }

    /* ---- Helpers ----------------------------------------------------------------------- */

    private static Map<String, Object> toMap(String code, String updatedAt, String language,
                                             String section, String path) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("code", code == null ? "" : code);
        out.put("updatedAt", updatedAt == null ? "" : updatedAt);
        out.put("language", (language == null || language.isBlank()) ? "python" : language);
        out.put("section", section == null ? "" : section);
        out.put("path", path == null ? "" : path);
        return out;
    }

    private static String prop(TableEntity e, String name) {
        Object v = e.getProperty(name);
        return v == null ? "" : v.toString();
    }

    private static boolean boolProp(TableEntity e, String name) {
        Object value = e.getProperty(name);
        return value instanceof Boolean b ? b : Boolean.parseBoolean(value == null ? "false" : value.toString());
    }

    private static Map<String, Object> historyMap(String code, String language, String updatedAt,
                                                   boolean passed) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("code", code == null ? "" : code);
        out.put("language", language(language));
        out.put("updatedAt", updatedAt == null ? "" : updatedAt);
        out.put("passed", passed);
        return out;
    }

    private static String language(String language) {
        return language == null || language.isBlank() ? "python" : language;
    }

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

    private record SavedSolution(String path, String section, String code, String language,
                                 String updatedAt) { }

    private record HistoryEntry(String code, String language, String updatedAt, boolean passed) { }
}
