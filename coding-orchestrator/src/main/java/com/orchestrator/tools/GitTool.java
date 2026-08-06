package com.orchestrator.tools;

import com.orchestrator.approval.ApprovalPolicy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Deterministic git tool wrapping the local {@code git} CLI. Supports init/add/commit; pushing to a
 * remote is gated by {@link ApprovalPolicy#autoApproveGitPush()} which defaults to {@code false}
 * (push is intentionally out of scope for now — tests are generated and committed locally only).
 */
public final class GitTool implements Tool {

    private final Path repoDir;
    private final ApprovalPolicy policy;

    public GitTool(Path repoDir, ApprovalPolicy policy) {
        this.repoDir = repoDir.toAbsolutePath().normalize();
        this.policy = policy;
    }

    @Override
    public String name() {
        return "git";
    }

    @Override
    public String description() {
        return "Run local git operations (init, add, commit). Remote push is policy-gated.";
    }

    @Override
    public ToolResult execute(Map<String, Object> args) {
        String op = String.valueOf(args.getOrDefault("op", "")).toLowerCase();
        return switch (op) {
            case "init" -> init();
            case "commit" -> commitAll(String.valueOf(args.getOrDefault("message", "orchestrator commit")));
            case "push" -> push(String.valueOf(args.getOrDefault("remote", "origin")),
                    String.valueOf(args.getOrDefault("branch", "main")));
            default -> ToolResult.error("unknown git op: " + op);
        };
    }

    public ToolResult init() {
        return run(List.of("git", "init"));
    }

    public ToolResult commitAll(String message) {
        if (!policy.autoApproveGitCommit()) {
            return ToolResult.error("git commit not approved by ApprovalPolicy");
        }
        ToolResult add = run(List.of("git", "add", "-A"));
        if (!add.success()) {
            return add;
        }
        // Configure identity locally so commit works in a fresh sandbox without global config.
        run(List.of("git", "config", "user.email", "orchestrator@example.com"));
        run(List.of("git", "config", "user.name", "Coding Orchestrator"));
        return run(List.of("git", "commit", "-m", message, "--allow-empty"));
    }

    public ToolResult push(String remote, String branch) {
        if (!policy.autoApproveGitPush()) {
            return ToolResult.error("git push not approved by ApprovalPolicy (out of scope)");
        }
        return run(List.of("git", "push", remote, branch));
    }

    private ToolResult run(List<String> command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(repoDir.toFile());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String out = new String(p.getInputStream().readAllBytes());
            boolean finished = p.waitFor(60, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return ToolResult.error("git command timed out: " + String.join(" ", command));
            }
            int code = p.exitValue();
            String label = String.join(" ", command);
            return code == 0
                    ? ToolResult.ok(label + "\n" + out.trim())
                    : ToolResult.error(label + " (exit " + code + ")\n" + out.trim());
        } catch (IOException e) {
            return ToolResult.error("git not available: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.error("interrupted");
        }
    }

    public Path repoDir() {
        return repoDir;
    }
}
