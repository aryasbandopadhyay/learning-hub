package com.example.hub.controller;

import com.example.hub.service.JudgeService;
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

    public JudgeController(JudgeService judge) {
        this.judge = judge;
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

    /**
     * Run a submission. Body: {@code {"path": "...", "code": "...", "mode": "run"|"complexity"}}.
     * {@code @RequestBody} binds the JSON body to a Map (Jackson).
     */
    @PostMapping("/run")
    public Map<String, Object> run(@RequestBody Map<String, String> body) {
        return judge.run(body.get("path"), body.get("code"), body.getOrDefault("mode", "run"));
    }
}
