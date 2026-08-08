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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================================
 * ReviewService — SM-2 spaced-repetition scheduling for completed study problems.
 * ============================================================================================
 * <p>Each user/problem pair owns one review row. The service applies the classic SM-2 quality
 * update (0..5), stores the next UTC due date, and exposes due/all/reset operations. As with the
 * other learning-state services, a blank Azure connection string selects an in-memory fallback.
 *
 * <h3>Table schema</h3>
 * <ul>
 *   <li><b>PartitionKey</b> = normalized user id; <b>RowKey</b> = Base64(path).</li>
 *   <li>Properties: {@code Path}, {@code Section}, {@code Ease}, {@code IntervalDays},
 *       {@code Reps}, {@code DueDate}, {@code LastReviewed}.</li>
 * </ul>
 */
@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final TableClient table;
    private final Map<String, Map<String, ReviewRow>> memory = new ConcurrentHashMap<>();

    public ReviewService(ProgressProperties props) {
        TableClient client = null;
        if (Boolean.TRUE.equals(props.enabled())
                && props.connectionString() != null && !props.connectionString().isBlank()) {
            try {
                client = new TableClientBuilder()
                        .connectionString(props.connectionString())
                        .tableName(props.reviewsTable())
                        .buildClient();
                try {
                    client.createTable();
                } catch (Exception alreadyExists) {
                    /* table is already provisioned — fine */
                }
                log.info("ReviewService: using Azure Table '{}' for review schedules.",
                        props.reviewsTable());
            } catch (Exception e) {
                log.warn("ReviewService: could not init Azure Table ({}); falling back to in-memory store.",
                        e.getMessage());
                client = null;
            }
        } else {
            log.info("ReviewService: no connection string configured — using in-memory store.");
        }
        this.table = client;
    }

    /* ---- Public API -------------------------------------------------------------------- */

    /**
     * Apply one SM-2 grade. Failed recalls restart repetitions at a one-day interval; successful
     * recalls advance through 1 day, 6 days, then the previous interval multiplied by ease.
     */
    public Map<String, Object> grade(String user, String path, String section, int quality) {
        String u = norm(user);
        if (path == null || path.isBlank()) return result("", 0, 2.5, 0);
        int q = Math.max(0, Math.min(5, quality));
        ReviewRow old = find(u, path);
        double oldEase = old == null ? 2.5 : old.ease();
        int reps;
        int interval;
        if (q < 3) {
            reps = 0;
            interval = 1;
        } else {
            reps = (old == null ? 0 : old.reps()) + 1;
            interval = reps == 1 ? 1
                    : reps == 2 ? 6
                    : (int) Math.round((old == null ? 1 : old.intervalDays()) * oldEase);
        }
        double delta = 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02);
        double ease = Math.max(1.3, oldEase + delta);
        String dueDate = LocalDate.now(ZoneOffset.UTC).plusDays(interval).toString();
        String reviewed = OffsetDateTime.now(ZoneOffset.UTC).toString();
        ReviewRow row = new ReviewRow(path, section == null ? "" : section, ease, interval,
                reps, dueDate, reviewed);
        put(u, row);
        return result(dueDate, interval, ease, reps);
    }

    /** Rows whose due date is today or earlier, ordered oldest due date first. */
    public List<Map<String, Object>> due(String user) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> row : all(user)) {
            LocalDate due = parseDate(str(row.get("dueDate")));
            if (due != null && !due.isAfter(today)) {
                Map<String, Object> item = new LinkedHashMap<>(row);
                item.remove("lastReviewed");
                out.add(item);
            }
        }
        out.sort(Comparator.comparing(row -> str(row.get("dueDate"))));
        return out;
    }

    /** Return all review rows for a user. */
    public List<Map<String, Object>> all(String user) {
        String u = norm(user);
        List<Map<String, Object>> out = new ArrayList<>();
        if (table != null) {
            String filter = "PartitionKey eq '" + escape(u) + "'";
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                out.add(toMap(fromEntity(e)));
            }
        } else {
            memory.getOrDefault(u, Map.of()).values().forEach(row -> out.add(toMap(row)));
        }
        return out;
    }

    /** Delete every review row in the user's partition. */
    public int reset(String user) {
        String u = norm(user);
        int removed = 0;
        if (table != null) {
            String filter = "PartitionKey eq '" + escape(u) + "'";
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                try {
                    table.deleteEntity(e.getPartitionKey(), e.getRowKey());
                    removed++;
                } catch (Exception ignore) {
                    /* concurrent deletion — ignore */
                }
            }
        } else {
            Map<String, ReviewRow> rows = memory.get(u);
            if (rows != null) {
                removed = rows.size();
                rows.clear();
            }
        }
        return removed;
    }

    /** Merge one exported review row without recalculating its schedule. */
    public void importRow(String user, Map<String, Object> data) {
        if (data == null) return;
        String path = str(data.get("path"));
        if (path == null || path.isBlank()) return;
        ReviewRow row = new ReviewRow(
                path,
                defaultString(data.get("section")),
                number(data.get("ease"), 2.5).doubleValue(),
                number(data.get("intervalDays"), 1).intValue(),
                number(data.get("reps"), 0).intValue(),
                defaultString(data.get("dueDate")),
                defaultString(data.get("lastReviewed")));
        put(norm(user), row);
    }

    /* ---- Persistence helpers ----------------------------------------------------------- */

    private ReviewRow find(String user, String path) {
        if (table != null) {
            try {
                return fromEntity(table.getEntity(user, rowKey(path)));
            } catch (HttpResponseException missing) {
                if (missing.getResponse() != null && missing.getResponse().getStatusCode() == 404) {
                    return null;
                }
                throw missing;
            }
        }
        return memory.getOrDefault(user, Map.of()).get(path);
    }

    private void put(String user, ReviewRow row) {
        if (table != null) {
            table.upsertEntity(new TableEntity(user, rowKey(row.path()))
                    .addProperty("Path", row.path())
                    .addProperty("Section", row.section())
                    .addProperty("Ease", row.ease())
                    .addProperty("IntervalDays", row.intervalDays())
                    .addProperty("Reps", row.reps())
                    .addProperty("DueDate", row.dueDate())
                    .addProperty("LastReviewed", row.lastReviewed()));
        } else {
            memory.computeIfAbsent(user, k -> new ConcurrentHashMap<>()).put(row.path(), row);
        }
    }

    private static ReviewRow fromEntity(TableEntity e) {
        return new ReviewRow(
                prop(e, "Path"),
                prop(e, "Section"),
                number(e.getProperty("Ease"), 2.5).doubleValue(),
                number(e.getProperty("IntervalDays"), 1).intValue(),
                number(e.getProperty("Reps"), 0).intValue(),
                prop(e, "DueDate"),
                prop(e, "LastReviewed"));
    }

    private static Map<String, Object> toMap(ReviewRow row) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("path", row.path());
        out.put("section", row.section());
        out.put("dueDate", row.dueDate());
        out.put("intervalDays", row.intervalDays());
        out.put("ease", row.ease());
        out.put("reps", row.reps());
        out.put("lastReviewed", row.lastReviewed());
        return out;
    }

    private static Map<String, Object> result(String dueDate, int interval, double ease, int reps) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("ok", true);
        out.put("dueDate", dueDate);
        out.put("intervalDays", interval);
        out.put("ease", ease);
        out.put("reps", reps);
        return out;
    }

    private static Number number(Object value, Number fallback) {
        if (value instanceof Number n) return n;
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignore) {
                /* use fallback */
            }
        }
        return fallback;
    }

    private static LocalDate parseDate(String value) {
        try {
            return value == null || value.isBlank() ? null : LocalDate.parse(value);
        } catch (RuntimeException invalid) {
            return null;
        }
    }

    private static String prop(TableEntity e, String name) {
        return defaultString(e.getProperty(name));
    }

    private static String defaultString(Object value) {
        return value == null ? "" : value.toString();
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }

    private static String norm(String user) {
        return user == null || user.isBlank() ? "default" : user;
    }

    private static String rowKey(String path) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(path.getBytes(StandardCharsets.UTF_8));
    }

    private static String escape(String value) {
        return value.replace("'", "''");
    }

    private record ReviewRow(String path, String section, double ease, int intervalDays, int reps,
                             String dueDate, String lastReviewed) { }
}
