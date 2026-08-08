package com.example.hub.controller;

import com.example.hub.service.AuthService;
import com.example.hub.service.JudgeService;
import com.example.hub.service.SolutionsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * ============================================================================================
 * JudgeController — REST API for the local DSA online judge.
 * ============================================================================================
 * <ul>
 *   <li>{@code GET  /api/judge/problem?path=dsa/arrays-hashing/01-two-sum.md} — metadata:
 *       starter stub, signature, sample tests, reference solutions (for the compare panel).</li>
 *   <li>{@code POST /api/judge/run} — grade a submission ({@code mode=run}) or estimate its
 *       time/space complexity ({@code mode=complexity}).</li>
 * </ul>
 * Thin controller; all logic lives in {@link JudgeService} (constructor-injected).
 */
@RestController
@RequestMapping("/api/judge")
public class JudgeController {

    private final JudgeService judge;
    private final SolutionsService solutions;
    private final ObjectMapper mapper;

    public JudgeController(JudgeService judge, SolutionsService solutions, ObjectMapper mapper) {
        this.judge = judge;
        this.solutions = solutions;
        this.mapper = mapper;
    }

    /** Editor-facing metadata for a problem (or {@code available:false} if no manifest exists). */
    @GetMapping("/problem")
    public Map<String, Object> problem(@RequestParam("path") String path) {
        return judge.problem(path);
    }

    /**
     * Lightweight catalogue of gradable problems for the dashboard, optionally scoped to a section
     * ({@code dsa} | {@code google} | {@code faang}). Each entry: {@code {path,title,difficulty,topic,section}}.
     */
    @GetMapping("/index")
    public Map<String, Object> index(@RequestParam(value = "section", required = false) String section) {
        var problems = judge.index(section);
        return Map.of("problems", problems, "count", problems.size());
    }

    /** Current user's saved all-tests-passing solution for this problem, if one exists. */
    @GetMapping("/solution")
    public Map<String, Object> solution(@RequestParam("path") String path, HttpServletRequest req) {
        return solutions.get(user(req), path)
                .<Map<String, Object>>map(s -> Map.of(
                        "saved", true,
                        "code", s.getOrDefault("code", ""),
                        "updatedAt", s.getOrDefault("updatedAt", ""),
                        "language", s.getOrDefault("language", "python")))
                .orElseGet(() -> Map.of("saved", false));
    }

    /** Newest-first accepted-submission history, capped at fifteen entries. */
    @GetMapping("/history")
    public Map<String, Object> history(@RequestParam("path") String path, HttpServletRequest req) {
        var history = solutions.history(user(req), path, 15);
        return Map.of("history", history, "count", history.size());
    }

    /** Reveal up to five hidden/generated tests for deliberate study after an attempt. */
    @GetMapping("/reveal")
    public Map<String, Object> reveal(@RequestParam("path") String path,
                                      @RequestParam(value = "n", defaultValue = "2") int n) {
        int count = Math.max(1, Math.min(5, n));
        var tests = judge.revealTests(path, count);
        return Map.of("tests", tests, "count", tests.size());
    }

    /**
     * Run a submission. Body: {@code {"path": "...", "code": "...", "mode": "run"|"complexity"}}.
     * {@code @RequestBody} binds the JSON body to a Map (Jackson).
     */
    @PostMapping("/run")
    public Map<String, Object> run(@RequestBody Map<String, String> body, HttpServletRequest req) {
        String path = body.get("path");
        String code = body.get("code");
        String mode = body.getOrDefault("mode", "run");
        // Opt-out flag for automated verification: a caller (e.g. a test harness) can send
        // "persist":"false" so its runs are NOT written to the user's saved-solutions store.
        // The UI never sends this, so a real user's passing submissions still save normally.
        boolean persist = !"false".equalsIgnoreCase(body.getOrDefault("persist", "true"));
        Map<String, Object> result = judge.run(path, code, mode);
        if (persist && "run".equals(mode) && allPassed(result)) {
            solutions.save(user(req), path, section(path), code, "python");
        }
        return result;
    }

    /**
     * Run one custom argument array. The browser sends {@code input} as JSON text (for example
     * {@code [[2,7,11,15],9]}); validating it here prevents malformed runner input documents.
     */
    @PostMapping("/custom")
    public Map<String, Object> custom(@RequestBody Map<String, Object> body) {
        String input = str(body.get("input"));
        JsonNode args;
        try {
            args = mapper.readTree(input == null ? "" : input);
        } catch (JsonProcessingException invalid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "input must be a valid JSON array", invalid);
        }
        if (args == null || !args.isArray()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "input must be a valid JSON array");
        }
        ObjectNode document = mapper.createObjectNode();
        document.set("args", args);
        return judge.runCustom(str(body.get("path")), str(body.get("code")), document.toString());
    }

    /* ---- Helpers ----------------------------------------------------------------------- */

    /** Resolve the caller exactly like ProgressController: session email, Easy Auth, default. */
    private static String user(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object email = session.getAttribute(AuthService.SESSION_EMAIL);
            if (email != null && !email.toString().isBlank()) return email.toString();
        }
        String principal = req.getHeader("X-MS-CLIENT-PRINCIPAL-NAME");
        return (principal == null || principal.isBlank()) ? "default" : principal;
    }

    /** Keep section names aligned with the judge index/progress dashboard categories. */
    private static String section(String path) {
        if (path == null || path.isBlank()) return "";
        String norm = path.replace('\\', '/');
        if (norm.startsWith("dsa/google/")) return "google";
        if (norm.startsWith("dsa/faang/")) return "faang";
        int slash = norm.indexOf('/');
        return slash < 0 ? norm : norm.substring(0, slash);
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }

    @SuppressWarnings("unchecked")
    private static boolean allPassed(Map<String, Object> result) {
        Object summary = result == null ? null : result.get("summary");
        if (!(summary instanceof Map<?, ?> m)) return false;
        return Boolean.TRUE.equals(m.get("allPassed"));
    }
}
