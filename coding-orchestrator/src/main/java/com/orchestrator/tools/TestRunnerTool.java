package com.orchestrator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Deterministic test-runner tool. Executes {@code mvn -q test} inside the workspace when a Maven
 * project is present; otherwise returns a clear, non-failing "skipped" result. Contains no LLM
 * logic — it is a reproducible script the agents can invoke to validate generated tests.
 */
public final class TestRunnerTool implements Tool {

    private final Path workspace;

    public TestRunnerTool(Path workspace) {
        this.workspace = workspace.toAbsolutePath().normalize();
    }

    @Override
    public String name() {
        return "run_tests";
    }

    @Override
    public String description() {
        return "Run the generated unit tests via 'mvn test' inside the workspace (or skip if no build).";
    }

    @Override
    public ToolResult execute(Map<String, Object> args) {
        return run();
    }

    public ToolResult run() {
        if (!Files.exists(workspace.resolve("pom.xml"))) {
            return ToolResult.ok("skipped: no pom.xml in workspace (nothing to build)");
        }
        String mvn = isWindows() ? "mvn.cmd" : "mvn";
        try {
            ProcessBuilder pb = new ProcessBuilder(List.of(mvn, "-q", "test"));
            pb.directory(workspace.toFile());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String out = new String(p.getInputStream().readAllBytes());
            boolean finished = p.waitFor(15, TimeUnit.MINUTES);
            if (!finished) {
                p.destroyForcibly();
                return ToolResult.error("test run timed out");
            }
            int code = p.exitValue();
            String tail = out.length() > 4000 ? out.substring(out.length() - 4000) : out;
            return code == 0 ? ToolResult.ok(tail) : ToolResult.error("tests failed (exit " + code + ")\n" + tail);
        } catch (IOException e) {
            return ToolResult.ok("skipped: maven not available (" + e.getMessage() + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.error("interrupted");
        }
    }

    private static boolean isWindows() {
        return File.separatorChar == '\\';
    }
}
