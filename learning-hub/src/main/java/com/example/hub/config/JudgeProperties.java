package com.example.hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ============================================================================================
 * JudgeProperties — typed binding of the {@code hub.judge.*} keys in application.yml.
 * ============================================================================================
 * <p>The local DSA "online judge" shells out to a small standalone Python engine
 * ({@code judge/runner.py}) that executes a user's submission against a per-problem
 * <em>manifest</em> (JSON test spec) and, optionally, estimates time/space complexity.
 * All of the wiring is configuration, so nothing is hard-coded:
 *
 * <pre>
 *   hub:
 *     judge:
 *       enabled: true
 *       python-exe: python
 *       judge-dir: judge            # holds runner.py
 *       manifests-dir: judge/manifests
 *       timeout-seconds: 15
 * </pre>
 *
 * @param enabled        master on/off switch for the judge feature.
 * @param pythonExe      the Python executable to invoke (e.g. {@code python} or an absolute path).
 * @param judgeDir       directory (relative to the working dir, or absolute) containing runner.py.
 * @param manifestsDir   directory holding the generated {@code <topic>/<slug>.json} manifests.
 * @param timeoutSeconds hard wall-clock cap for a single runner invocation.
 */
@ConfigurationProperties(prefix = "hub.judge")
public record JudgeProperties(
        Boolean enabled,
        String pythonExe,
        String judgeDir,
        String manifestsDir,
        Integer timeoutSeconds
) {
    /** Fill in safe defaults for any key omitted from application.yml. */
    public JudgeProperties {
        if (enabled == null) enabled = Boolean.TRUE;
        if (pythonExe == null || pythonExe.isBlank()) pythonExe = "python";
        if (judgeDir == null || judgeDir.isBlank()) judgeDir = "judge";
        if (manifestsDir == null || manifestsDir.isBlank()) manifestsDir = "judge/manifests";
        if (timeoutSeconds == null || timeoutSeconds <= 0) timeoutSeconds = 15;
    }
}
