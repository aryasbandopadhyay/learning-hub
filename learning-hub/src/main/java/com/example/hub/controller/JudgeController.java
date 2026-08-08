package com.example.hub.controller;

import com.example.hub.service.AuthService;
import com.example.hub.service.JudgeService;
import com.example.hub.service.SolutionsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    public JudgeController(JudgeService judge, SolutionsService solutions) {
        this.judge = judge;
        this.solutions = solutions;
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

    @SuppressWarnings("unchecked")
    private static boolean allPassed(Map<String, Object> result) {
        Object summary = result == null ? null : result.get("summary");
        if (!(summary instanceof Map<?, ?> m)) return false;
        return Boolean.TRUE.equals(m.get("allPassed"));
    }
}
