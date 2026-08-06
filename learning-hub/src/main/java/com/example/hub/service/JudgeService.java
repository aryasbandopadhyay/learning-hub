package com.example.hub.service;

import com.example.hub.config.JudgeProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ============================================================================================
 * JudgeService — the bridge between the web layer and the standalone Python judge engine.
 * ============================================================================================
 * The judge itself is intentionally NOT written in Java: {@code judge/runner.py} is a tiny,
 * dependency-free Python program that compiles a submission, runs it against a manifest's test
 * cases, and (optionally) estimates Big-O empirically. This service:
 *
 * <ol>
 *   <li>Maps a DSA markdown path ({@code dsa/<topic>/<slug>.md}) to its manifest
 *       ({@code judge/manifests/<topic>/<slug>.json}).</li>
 *   <li>Serves problem <em>metadata</em> (starter stub, signature, sample tests, and the three
 *       reference solutions for the side-by-side compare) WITHOUT leaking hidden test data.</li>
 *   <li>Runs a submission by writing it to a temp file and invoking runner.py as a subprocess
 *       with a hard timeout, then returns the runner's JSON verbatim.</li>
 * </ol>
 *
 * Everything is sandboxed only as much as a local single-user learning tool needs; this is a
 * personal study aid, not a public code-execution service.
 */
@Service
public class JudgeService {

    private final JudgeProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    private final Path judgeDir;
    private final Path runner;
    private final Path manifestsDir;

    public JudgeService(JudgeProperties props) {
        this.props = props;
        Path work = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        this.judgeDir = work.resolve(props.judgeDir()).normalize();
        this.runner = judgeDir.resolve("runner.py").normalize();
        this.manifestsDir = work.resolve(props.manifestsDir()).normalize();
    }

    public boolean enabled() {
        return Boolean.TRUE.equals(props.enabled());
    }

    // -----------------------------------------------------------------------------------------
    // Manifest resolution
    // -----------------------------------------------------------------------------------------

    /**
     * Map a content path such as {@code dsa/arrays-hashing/01-two-sum.md} to its manifest file.
     * We mirror every segment AFTER the first (the category root, e.g. {@code dsa/}) into the
     * manifests directory. So {@code dsa/arrays-hashing/01-two-sum.md} -> {@code arrays-hashing/
     * 01-two-sum.json} (unchanged), while nested sections like {@code dsa/google/arrays/two-sum.md}
     * map to {@code google/arrays/two-sum.json} — namespaced, so they never collide with the main
     * bank. Returns null if the manifest does not exist yet.
     */
    private Path manifestForPath(String contentPath) {
        if (contentPath == null || contentPath.isBlank()) {
            return null;
        }
        String norm = contentPath.replace('\\', '/');
        String[] parts = norm.split("/");
        if (parts.length < 2) {
            return null;
        }
        // Everything after the leading category-root segment (e.g. "dsa").
        Path mf = manifestsDir;
        for (int i = 1; i < parts.length; i++) {
            String seg = parts[i];
            if (seg.isBlank() || seg.equals("..") || seg.equals(".")) {
                return null;
            }
            if (i == parts.length - 1) {
                seg = seg.replaceFirst("\\.[^.]+$", "") + ".json"; // strip ext, add .json
            }
            mf = mf.resolve(seg);
        }
        mf = mf.normalize();
        // Guard against traversal: the resolved manifest must stay under manifestsDir.
        if (!mf.startsWith(manifestsDir) || !Files.isRegularFile(mf)) {
            return null;
        }
        return mf;
    }

    private JsonNode readManifest(Path mf) {
        try {
            return mapper.readTree(Files.readString(mf, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read manifest", e);
        }
    }

    // -----------------------------------------------------------------------------------------
    // Public API: problem metadata
    // -----------------------------------------------------------------------------------------

    /**
     * Return the editor-facing metadata for a problem: whether a judge exists, the starter stub,
     * the entry signature, the visible (example) tests, and the three reference solutions (for the
     * side-by-side compare). Hidden random tests are intentionally NOT included.
     */
    public Map<String, Object> problem(String contentPath) {
        ObjectNode out = mapper.createObjectNode();
        out.put("path", contentPath == null ? "" : contentPath);

        if (!enabled()) {
            out.put("available", false);
            out.put("reason", "Judge is disabled.");
            return mapper.convertValue(out, Map.class);
        }
        Path mf = manifestForPath(contentPath);
        if (mf == null) {
            out.put("available", false);
            out.put("reason", "No judge manifest for this problem yet.");
            return mapper.convertValue(out, Map.class);
        }

        JsonNode m = readManifest(mf);
        out.put("available", true);
        out.put("id", m.path("id").asText(""));
        out.put("title", m.path("title").asText(""));
        out.put("difficulty", m.path("difficulty").asText(""));
        out.put("topic", m.path("topic").asText(""));
        out.put("shape", m.path("shape").asText(""));
        out.put("entry", m.path("entry").asText(""));
        out.put("className", m.path("className").asText("Solution"));
        out.put("starterCode", m.path("starterCode").asText(""));
        out.put("complexitySupported", m.path("complexity").path("supported").asBoolean(false));

        // Visible sample tests = those flagged kind == "example".
        var samples = mapper.createArrayNode();
        for (JsonNode t : m.path("tests")) {
            if ("example".equals(t.path("kind").asText())) {
                samples.add(t);
            }
        }
        out.set("sampleTests", samples);

        // Reference solutions for the compare panel.
        JsonNode sols = m.path("solutions");
        ObjectNode solOut = mapper.createObjectNode();
        for (String key : new String[]{"naive", "better", "optimal"}) {
            if (sols.has(key)) {
                solOut.put(key, sols.path(key).asText(""));
            }
        }
        out.set("solutions", solOut);
        return mapper.convertValue(out, Map.class);
    }

    // -----------------------------------------------------------------------------------------
    // Public API: problem index (for the dashboard)
    // -----------------------------------------------------------------------------------------

    /** Cached full problem index (manifests are static at runtime, so compute once). */
    private volatile java.util.List<Map<String, Object>> indexCache;

    /**
     * A lightweight catalogue of every gradable problem for the "Problems" dashboard:
     * {@code [{path, title, difficulty, topic, section}, ...]}. Optionally filtered to one
     * section id ({@code dsa} | {@code google} | {@code faang}).
     *
     * <p>Built by walking the manifests directory and reading each manifest's title/difficulty/
     * topic. The manifest relative path is mapped back to its content path — the inverse of
     * {@link #manifestForPath}: {@code <rel>.json} -> {@code dsa/<rel>.md}. Manifests under
     * {@code google/} or {@code faang/} belong to those sections; everything else is the main
     * DSA bank.
     */
    public java.util.List<Map<String, Object>> index(String section) {
        java.util.List<Map<String, Object>> all = fullIndex();
        if (section == null || section.isBlank()) return all;
        java.util.List<Map<String, Object>> out = new java.util.ArrayList<>();
        for (Map<String, Object> p : all) {
            if (section.equals(p.get("section"))) out.add(p);
        }
        return out;
    }

    private java.util.List<Map<String, Object>> fullIndex() {
        java.util.List<Map<String, Object>> cache = indexCache;
        if (cache != null) return cache;
        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        if (enabled() && Files.isDirectory(manifestsDir)) {
            try (java.util.stream.Stream<Path> stream = Files.walk(manifestsDir)) {
                stream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".json")).forEach(p -> {
                    try {
                        JsonNode m = mapper.readTree(Files.readString(p, StandardCharsets.UTF_8));
                        String rel = manifestsDir.relativize(p).toString().replace('\\', '/');
                        String noExt = rel.replaceFirst("\\.json$", "");
                        String content = "dsa/" + noExt + ".md";
                        String section = noExt.startsWith("google/") ? "google"
                                : noExt.startsWith("faang/") ? "faang" : "dsa";
                        Map<String, Object> item = new java.util.LinkedHashMap<>();
                        item.put("path", content);
                        item.put("title", m.path("title").asText(""));
                        item.put("difficulty", m.path("difficulty").asText(""));
                        item.put("topic", m.path("topic").asText(""));
                        item.put("shape", m.path("shape").asText(""));
                        item.put("section", section);
                        list.add(item);
                    } catch (IOException ignore) {
                        /* skip an unreadable manifest */
                    }
                });
            } catch (IOException ignore) {
                /* manifests dir vanished — return what we have */
            }
        }
        list.sort(java.util.Comparator
                .comparing((Map<String, Object> x) -> String.valueOf(x.get("topic")))
                .thenComparing(x -> String.valueOf(x.get("path"))));
        indexCache = list;
        return list;
    }

    // -----------------------------------------------------------------------------------------
    // Public API: run a submission
    // -----------------------------------------------------------------------------------------

    /**
     * Execute a submission against a problem's manifest. {@code mode} is "run" (grade against test
     * cases) or "complexity" (empirical Big-O estimate). Returns the runner's JSON as a Map.
     */
    public Map<String, Object> run(String contentPath, String code, String mode) {
        if (!enabled()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Judge is disabled");
        }
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");
        }
        String runMode = "complexity".equals(mode) ? "complexity" : "run";

        Path mf = manifestForPath(contentPath);
        if (mf == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No judge manifest for this problem");
        }
        if (!Files.isRegularFile(runner)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Judge runner not found at " + runner);
        }

        Path submission = null;
        Process proc = null;
        try {
            submission = Files.createTempFile("submission-", ".py");
            Files.writeString(submission, code, StandardCharsets.UTF_8);

            ProcessBuilder pb = new ProcessBuilder(
                    props.pythonExe(),
                    runner.toString(),
                    "--manifest", mf.toString(),
                    "--submission", submission.toString(),
                    "--mode", runMode);
            pb.directory(judgeDir.toFile());
            pb.redirectErrorStream(false);
            proc = pb.start();

            String stdout = new String(proc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String stderr = new String(proc.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            boolean finished = proc.waitFor(props.timeoutSeconds(), TimeUnit.SECONDS);
            if (!finished) {
                proc.destroyForcibly();
                ObjectNode timeout = mapper.createObjectNode();
                timeout.put("ok", false);
                timeout.put("timeout", true);
                timeout.put("message", "Execution exceeded " + props.timeoutSeconds() + "s and was terminated.");
                return mapper.convertValue(timeout, Map.class);
            }

            if (stdout.isBlank()) {
                ObjectNode err = mapper.createObjectNode();
                err.put("ok", false);
                err.put("message", "Runner produced no output.");
                err.put("stderr", truncate(stderr, 4000));
                return mapper.convertValue(err, Map.class);
            }
            try {
                JsonNode result = mapper.readTree(stdout);
                return mapper.convertValue(result, Map.class);
            } catch (IOException parseErr) {
                ObjectNode err = mapper.createObjectNode();
                err.put("ok", false);
                err.put("message", "Could not parse runner output.");
                err.put("stdout", truncate(stdout, 4000));
                err.put("stderr", truncate(stderr, 4000));
                return mapper.convertValue(err, Map.class);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to run judge", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Judge interrupted", e);
        } finally {
            if (proc != null && proc.isAlive()) {
                proc.destroyForcibly();
            }
            if (submission != null) {
                try {
                    Files.deleteIfExists(submission);
                } catch (IOException ignore) {
                    // temp file cleanup is best-effort
                }
            }
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…(truncated)";
    }
}
