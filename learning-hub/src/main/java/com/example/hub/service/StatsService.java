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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================================
 * StatsService — records attempts, solves, timing, complexity notes and activity streaks.
 * ============================================================================================
 * <p>One row represents one problem for one user. Partial updates deliberately preserve fields
 * owned by other endpoints: an attempt never erases complexity reports, and a self-report never
 * resets attempts or solve timestamps. Azure Table Storage and the in-memory fallback expose the
 * same map-shaped API.
 */
@Service
public class StatsService {

    private static final Logger log = LoggerFactory.getLogger(StatsService.class);

    private final TableClient table;
    private final Map<String, Map<String, StatsRow>> memory = new ConcurrentHashMap<>();

    public StatsService(ProgressProperties props) {
        TableClient client = null;
        if (Boolean.TRUE.equals(props.enabled())
                && props.connectionString() != null && !props.connectionString().isBlank()) {
            try {
                client = new TableClientBuilder()
                        .connectionString(props.connectionString())
                        .tableName(props.statsTable())
                        .buildClient();
                try {
                    client.createTable();
                } catch (Exception alreadyExists) {
                    /* table is already provisioned — fine */
                }
                log.info("StatsService: using Azure Table '{}' for problem statistics.",
                        props.statsTable());
            } catch (Exception e) {
                log.warn("StatsService: could not init Azure Table ({}); falling back to in-memory store.",
                        e.getMessage());
                client = null;
            }
        } else {
            log.info("StatsService: no connection string configured — using in-memory store.");
        }
        this.table = client;
    }

    /* ---- Public API -------------------------------------------------------------------- */

    /** Record one judge attempt and return the fields the UI updates immediately. */
    public Map<String, Object> attempt(String user, String path, String section,
                                       boolean solved, long elapsedMs) {
        String u = norm(user);
        if (path == null || path.isBlank()) return attemptResult(empty(path, section));
        StatsRow old = find(u, path);
        if (old == null) old = empty(path, section);

        String now = OffsetDateTime.now(ZoneOffset.UTC).toString();
        boolean everSolved = old.solved() || solved;
        String firstSolvedAt = old.firstSolvedAt();
        long best = old.bestTimeMs();
        if (solved) {
            if (firstSolvedAt == null || firstSolvedAt.isBlank()) firstSolvedAt = now;
            if (elapsedMs > 0 && (best == 0 || elapsedMs < best)) best = elapsedMs;
        }
        String sec = section == null || section.isBlank() ? old.section() : section;
        StatsRow row = new StatsRow(path, sec, old.attempts() + 1, everSolved, firstSolvedAt,
                now, best, old.selfComplexity(), old.measuredComplexity());
        put(u, row);
        return attemptResult(row);
    }

    /** Save the learner's claimed and measured complexity without clobbering judge statistics. */
    public void selfReport(String user, String path, String self, String measured) {
        String u = norm(user);
        if (path == null || path.isBlank()) return;
        StatsRow old = find(u, path);
        if (old == null) old = empty(path, "");
        put(u, new StatsRow(old.path(), old.section(), old.attempts(), old.solved(),
                old.firstSolvedAt(), old.lastAttemptAt(), old.bestTimeMs(),
                self == null ? "" : self, measured == null ? "" : measured));
    }

    /** Return one complete statistics row. */
    public java.util.Optional<Map<String, Object>> get(String user, String path) {
        if (path == null || path.isBlank()) return java.util.Optional.empty();
        StatsRow row = find(norm(user), path);
        return row == null ? java.util.Optional.empty() : java.util.Optional.of(toMap(row));
    }

    /** Return every statistics row in the user's partition. */
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

    /**
     * Build a UTC solve calendar and streak summary. Multiple problems first solved on the same
     * date increment that day's count but contribute only one day to streak calculations.
     */
    public Map<String, Object> activity(String user) {
        Map<String, Integer> days = new TreeMap<>();
        Set<LocalDate> solveDays = new HashSet<>();
        int totalSolved = 0;
        for (Map<String, Object> row : all(user)) {
            if (!Boolean.TRUE.equals(row.get("solved"))) continue;
            totalSolved++;
            LocalDate date = utcDate(str(row.get("firstSolvedAt")));
            if (date != null) {
                solveDays.add(date);
                days.merge(date.toString(), 1, Integer::sum);
            }
        }

        int longest = 0;
        for (LocalDate date : solveDays) {
            if (solveDays.contains(date.minusDays(1))) continue;
            int length = 1;
            while (solveDays.contains(date.plusDays(length))) length++;
            longest = Math.max(longest, length);
        }

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate cursor = solveDays.contains(today) ? today
                : solveDays.contains(today.minusDays(1)) ? today.minusDays(1) : null;
        int current = 0;
        while (cursor != null && solveDays.contains(cursor)) {
            current++;
            cursor = cursor.minusDays(1);
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("days", days);
        out.put("currentStreak", current);
        out.put("longestStreak", longest);
        out.put("totalSolved", totalSolved);
        return out;
    }

    /** Merge one exported row exactly, preserving its historical counters and timestamps. */
    public void importRow(String user, Map<String, Object> data) {
        if (data == null) return;
        String path = str(data.get("path"));
        if (path == null || path.isBlank()) return;
        StatsRow row = new StatsRow(
                path,
                text(data.get("section")),
                number(data.get("attempts"), 0).intValue(),
                bool(data.get("solved")),
                text(data.get("firstSolvedAt")),
                text(data.get("lastAttemptAt")),
                number(data.get("bestTimeMs"), 0L).longValue(),
                text(data.get("selfComplexity")),
                text(data.get("measuredComplexity")));
        put(norm(user), row);
    }

    /* ---- Persistence helpers ----------------------------------------------------------- */

    private StatsRow find(String user, String path) {
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

    private void put(String user, StatsRow row) {
        if (table != null) {
            table.upsertEntity(new TableEntity(user, rowKey(row.path()))
                    .addProperty("Path", row.path())
                    .addProperty("Section", row.section())
                    .addProperty("Attempts", row.attempts())
                    .addProperty("Solved", row.solved())
                    .addProperty("FirstSolvedAt", row.firstSolvedAt())
                    .addProperty("LastAttemptAt", row.lastAttemptAt())
                    .addProperty("BestTimeMs", row.bestTimeMs())
                    .addProperty("SelfComplexity", row.selfComplexity())
                    .addProperty("MeasuredComplexity", row.measuredComplexity()));
        } else {
            memory.computeIfAbsent(user, k -> new ConcurrentHashMap<>()).put(row.path(), row);
        }
    }

    private static StatsRow fromEntity(TableEntity e) {
        return new StatsRow(
                prop(e, "Path"),
                prop(e, "Section"),
                number(e.getProperty("Attempts"), 0).intValue(),
                bool(e.getProperty("Solved")),
                prop(e, "FirstSolvedAt"),
                prop(e, "LastAttemptAt"),
                number(e.getProperty("BestTimeMs"), 0L).longValue(),
                prop(e, "SelfComplexity"),
                prop(e, "MeasuredComplexity"));
    }

    private static StatsRow empty(String path, String section) {
        return new StatsRow(path == null ? "" : path, section == null ? "" : section,
                0, false, "", "", 0L, "", "");
    }

    private static Map<String, Object> attemptResult(StatsRow row) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("ok", true);
        out.put("attempts", row.attempts());
        out.put("bestTimeMs", row.bestTimeMs());
        out.put("firstSolvedAt", row.firstSolvedAt());
        out.put("solved", row.solved());
        return out;
    }

    /** Stable JSON field order makes exports and browser inspection easier to read. */
    private static Map<String, Object> toMap(StatsRow row) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("path", row.path());
        out.put("section", row.section());
        out.put("attempts", row.attempts());
        out.put("solved", row.solved());
        out.put("firstSolvedAt", row.firstSolvedAt());
        out.put("lastAttemptAt", row.lastAttemptAt());
        out.put("bestTimeMs", row.bestTimeMs());
        out.put("selfComplexity", row.selfComplexity());
        out.put("measuredComplexity", row.measuredComplexity());
        return out;
    }

    private static LocalDate utcDate(String value) {
        try {
            return value == null || value.isBlank() ? null
                    : OffsetDateTime.parse(value).withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
        } catch (RuntimeException invalid) {
            return null;
        }
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

    private static boolean bool(Object value) {
        return value instanceof Boolean b ? b : Boolean.parseBoolean(text(value));
    }

    private static String prop(TableEntity e, String name) {
        return text(e.getProperty(name));
    }

    private static String text(Object value) {
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

    private record StatsRow(String path, String section, int attempts, boolean solved,
                            String firstSolvedAt, String lastAttemptAt, long bestTimeMs,
                            String selfComplexity, String measuredComplexity) { }
}
