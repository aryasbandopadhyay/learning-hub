package com.example.hub.service;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.example.hub.config.AuthProperties;
import com.example.hub.config.ProgressProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================================
 * UserService — manages the email allow-list for the simple auth model.
 * ============================================================================================
 * <p>Allow-listed emails are stored in an Azure Storage Table (same account as progress, table
 * name from {@code hub.auth.users-table}); when no connection string is configured it falls back
 * to an in-memory set so local development still works.
 *
 * <h3>Table schema</h3>
 * <ul>
 *   <li><b>PartitionKey</b> = {@code "user"} (single logical bucket).</li>
 *   <li><b>RowKey</b> = the normalised (lower-cased) email — emails are valid RowKeys.</li>
 *   <li>Properties: {@code Email} (original), {@code AddedBy}, {@code AddedAt}.</li>
 * </ul>
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String PARTITION = "user";

    private final TableClient table;
    /** In-memory fallback: normalised email -> row (Email/AddedBy/AddedAt). */
    private final Map<String, Map<String, String>> memory = new ConcurrentHashMap<>();

    public UserService(AuthProperties auth, ProgressProperties progress) {
        TableClient client = null;
        String cs = progress.connectionString();
        if (cs != null && !cs.isBlank()) {
            try {
                client = new TableClientBuilder()
                        .connectionString(cs)
                        .tableName(auth.usersTable())
                        .buildClient();
                try {
                    client.createTable();
                } catch (Exception alreadyExists) {
                    /* already provisioned */
                }
                log.info("UserService: using Azure Table '{}' for the email allow-list.", auth.usersTable());
            } catch (Exception e) {
                log.warn("UserService: could not init Azure Table ({}); using in-memory allow-list.", e.getMessage());
                client = null;
            }
        } else {
            log.info("UserService: no connection string — using in-memory allow-list.");
        }
        this.table = client;
    }

    /* ---- API --------------------------------------------------------------------------- */

    /** True if this email has been allow-listed (case-insensitive). */
    public boolean isAllowed(String email) {
        String key = norm(email);
        if (key.isEmpty()) return false;
        if (table != null) {
            try {
                table.getEntity(PARTITION, key);
                return true;
            } catch (Exception notFound) {
                return false;
            }
        }
        return memory.containsKey(key);
    }

    /** Add (or refresh) an allow-listed email. Returns the normalised email. */
    public String add(String email, String addedBy) {
        String key = norm(email);
        if (key.isEmpty()) return null;
        String now = OffsetDateTime.now().toString();
        if (table != null) {
            TableEntity e = new TableEntity(PARTITION, key)
                    .addProperty("Email", email.trim())
                    .addProperty("AddedBy", addedBy == null ? "" : addedBy)
                    .addProperty("AddedAt", now);
            table.upsertEntity(e);
        } else {
            Map<String, String> row = new ConcurrentHashMap<>();
            row.put("Email", email.trim());
            row.put("AddedBy", addedBy == null ? "" : addedBy);
            row.put("AddedAt", now);
            memory.put(key, row);
        }
        return key;
    }

    /** Remove an allow-listed email. */
    public void remove(String email) {
        String key = norm(email);
        if (key.isEmpty()) return;
        if (table != null) {
            try {
                table.deleteEntity(PARTITION, key);
            } catch (Exception ignore) { /* already gone */ }
        } else {
            memory.remove(key);
        }
    }

    /** All allow-listed users (each: email, addedBy, addedAt). */
    public List<Map<String, Object>> list() {
        List<Map<String, Object>> out = new ArrayList<>();
        if (table != null) {
            String filter = "PartitionKey eq '" + PARTITION + "'";
            for (TableEntity e : table.listEntities(new ListEntitiesOptions().setFilter(filter), null, null)) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("email", str(e.getProperty("Email")));
                row.put("addedBy", str(e.getProperty("AddedBy")));
                row.put("addedAt", str(e.getProperty("AddedAt")));
                out.add(row);
            }
        } else {
            memory.forEach((k, v) -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("email", v.getOrDefault("Email", k));
                row.put("addedBy", v.getOrDefault("AddedBy", ""));
                row.put("addedAt", v.getOrDefault("AddedAt", ""));
                out.add(row);
            });
        }
        out.sort((a, b) -> String.valueOf(a.get("email")).compareToIgnoreCase(String.valueOf(b.get("email"))));
        return out;
    }

    /* ---- Helpers ----------------------------------------------------------------------- */

    private static String norm(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
