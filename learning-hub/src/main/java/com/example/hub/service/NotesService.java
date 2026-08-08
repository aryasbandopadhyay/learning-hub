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
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================================
 * NotesService — stores a user's free-form study note beside each problem.
 * ============================================================================================
 * <p>The persistence pattern intentionally matches {@link ProgressService}: Azure Table Storage
 * is used when the shared progress connection string is configured, while local development uses
 * a {@link ConcurrentHashMap}. A blank note means "delete", keeping the table free of empty rows.
 *
 * <h3>Table schema</h3>
 * <ul>
 *   <li><b>PartitionKey</b> = normalized user id.</li>
 *   <li><b>RowKey</b> = URL-safe Base64 of the content path.</li>
 *   <li>Properties: {@code Path}, {@code Text}, {@code UpdatedAt}.</li>
 * </ul>
 */
@Service
public class NotesService {

    private static final Logger log = LoggerFactory.getLogger(NotesService.class);
    private static final int MAX_TEXT_BYTES = 60_000;

    private final TableClient table;
    private final Map<String, Map<String, Note>> memory = new ConcurrentHashMap<>();

    public NotesService(ProgressProperties props) {
        TableClient client = null;
        if (Boolean.TRUE.equals(props.enabled())
                && props.connectionString() != null && !props.connectionString().isBlank()) {
            try {
                client = new TableClientBuilder()
                        .connectionString(props.connectionString())
                        .tableName(props.notesTable())
                        .buildClient();
                try {
                    client.createTable();
                } catch (Exception alreadyExists) {
                    /* table is already provisioned — fine */
                }
                log.info("NotesService: using Azure Table '{}' for study notes.", props.notesTable());
            } catch (Exception e) {
                log.warn("NotesService: could not init Azure Table ({}); falling back to in-memory store.",
                        e.getMessage());
                client = null;
            }
        } else {
            log.info("NotesService: no connection string configured — using in-memory store.");
        }
        this.table = client;
    }

    /* ---- Public API -------------------------------------------------------------------- */

    /** Insert/update a note; null or blank text deletes the existing note. */
    public void save(String user, String path, String text) {
        String u = norm(user);
        if (path == null || path.isBlank()) return;
        if (text == null || text.isBlank()) {
            if (table != null) {
                try {
                    table.deleteEntity(u, rowKey(path));
                } catch (Exception ignore) {
                    /* already absent — deletion is idempotent */
                }
            } else {
                Map<String, Note> notes = memory.get(u);
                if (notes != null) notes.remove(path);
            }
            return;
        }
        if (text.getBytes(StandardCharsets.UTF_8).length > MAX_TEXT_BYTES) {
            log.warn("NotesService: skipping oversized note for path '{}'.", path);
            return;
        }

        String updatedAt = OffsetDateTime.now(ZoneOffset.UTC).toString();
        if (table != null) {
            table.upsertEntity(new TableEntity(u, rowKey(path))
                    .addProperty("Path", path)
                    .addProperty("Text", text)
                    .addProperty("UpdatedAt", updatedAt));
        } else {
            memory.computeIfAbsent(u, k -> new ConcurrentHashMap<>())
                    .put(path, new Note(path, text, updatedAt));
        }
    }

    /** Return one note, or empty when no row exists. */
    public Optional<Map<String, Object>> get(String user, String path) {
        String u = norm(user);
        if (path == null || path.isBlank()) return Optional.empty();
        if (table != null) {
            try {
                TableEntity e = table.getEntity(u, rowKey(path));
                return Optional.of(toMap(prop(e, "Path"), prop(e, "Text"), prop(e, "UpdatedAt"), true));
            } catch (HttpResponseException missing) {
                if (missing.getResponse() != null && missing.getResponse().getStatusCode() == 404) {
                    return Optional.empty();
                }
                throw missing;
            }
        }
        Note note = memory.getOrDefault(u, Map.of()).get(path);
        return note == null ? Optional.empty()
                : Optional.of(toMap(note.path(), note.text(), note.updatedAt(), true));
    }

    /** Export-friendly list containing only the stable note data ({@code path} and {@code text}). */
    public List<Map<String, Object>> all(String user) {
        String u = norm(user);
        List<Map<String, Object>> out = new ArrayList<>();
        if (table != null) {
            String filter = "PartitionKey eq '" + escape(u) + "'";
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                out.add(toMap(prop(e, "Path"), prop(e, "Text"), "", false));
            }
        } else {
            memory.getOrDefault(u, Map.of()).values()
                    .forEach(n -> out.add(toMap(n.path(), n.text(), "", false)));
        }
        return out;
    }

    /* ---- Helpers ----------------------------------------------------------------------- */

    private static Map<String, Object> toMap(String path, String text, String updatedAt,
                                             boolean includeUpdatedAt) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("path", path == null ? "" : path);
        out.put("text", text == null ? "" : text);
        if (includeUpdatedAt) out.put("updatedAt", updatedAt == null ? "" : updatedAt);
        return out;
    }

    private static String prop(TableEntity e, String name) {
        Object value = e.getProperty(name);
        return value == null ? "" : value.toString();
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

    private record Note(String path, String text, String updatedAt) { }
}
