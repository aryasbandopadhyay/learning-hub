package com.orchestrator.tools;

import com.orchestrator.approval.ApprovalPolicy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Deterministic filesystem tool. All writes are confined to a sandbox workspace root and gated by
 * the front-loaded {@link ApprovalPolicy}. Path traversal outside the root is rejected.
 */
public final class FileSystemTool implements Tool {

    private final Path root;
    private final ApprovalPolicy policy;

    public FileSystemTool(Path root, ApprovalPolicy policy) {
        this.root = root.toAbsolutePath().normalize();
        this.policy = policy;
    }

    @Override
    public String name() {
        return "fs_write";
    }

    @Override
    public String description() {
        return "Write or read UTF-8 text files inside the orchestrator workspace sandbox.";
    }

    /** Convenience helper used by agents/tests to write a file relative to the workspace root. */
    public ToolResult writeFile(String relativePath, String content) {
        return execute(Map.of("op", "write", "path", relativePath, "content", content));
    }

    /** Convenience helper to read a file relative to the workspace root. */
    public ToolResult readFile(String relativePath) {
        return execute(Map.of("op", "read", "path", relativePath));
    }

    public Path root() {
        return root;
    }

    @Override
    public ToolResult execute(Map<String, Object> args) {
        String op = String.valueOf(args.getOrDefault("op", "write"));
        String rel = String.valueOf(args.getOrDefault("path", ""));
        if (rel.isBlank()) {
            return ToolResult.error("missing 'path'");
        }

        final Path target;
        try {
            target = resolveInsideRoot(rel);
        } catch (IllegalArgumentException e) {
            return ToolResult.error(e.getMessage());
        }

        try {
            if ("read".equalsIgnoreCase(op)) {
                if (!Files.exists(target)) {
                    return ToolResult.error("not found: " + rel);
                }
                return ToolResult.ok(Files.readString(target, StandardCharsets.UTF_8));
            }

            if (!policy.autoApproveFileWrites()) {
                return ToolResult.error("file writes not approved by ApprovalPolicy");
            }
            String content = String.valueOf(args.getOrDefault("content", ""));
            Files.createDirectories(target.getParent());
            Files.writeString(target, content, StandardCharsets.UTF_8);
            return ToolResult.ok("wrote " + root.relativize(target));
        } catch (IOException e) {
            return ToolResult.error("io error: " + e.getMessage());
        }
    }

    private Path resolveInsideRoot(String rel) {
        Path resolved = root.resolve(rel).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("path escapes workspace sandbox: " + rel);
        }
        return resolved;
    }
}
