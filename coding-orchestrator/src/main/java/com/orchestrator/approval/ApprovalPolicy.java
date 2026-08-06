package com.orchestrator.approval;

/**
 * Front-loaded approval policy: every human decision the run might need is captured <em>once</em>,
 * up front, so the pipeline can execute end-to-end without mid-run prompts ("minimize human touch").
 *
 * <p>Deterministic tools consult this policy before performing any side effect. The loop caps also
 * live here so the master state-machine terminates deterministically.
 */
public final class ApprovalPolicy {

    private final boolean autoApproveFileWrites;
    private final boolean autoApproveGitCommit;
    private final boolean autoApproveGitPush;
    private final boolean autoApproveModelCalls;
    private final int maxDesignIterations;
    private final int maxCodeIterations;

    public ApprovalPolicy(boolean autoApproveFileWrites,
                          boolean autoApproveGitCommit,
                          boolean autoApproveGitPush,
                          boolean autoApproveModelCalls,
                          int maxDesignIterations,
                          int maxCodeIterations) {
        this.autoApproveFileWrites = autoApproveFileWrites;
        this.autoApproveGitCommit = autoApproveGitCommit;
        this.autoApproveGitPush = autoApproveGitPush;
        this.autoApproveModelCalls = autoApproveModelCalls;
        this.maxDesignIterations = Math.max(1, maxDesignIterations);
        this.maxCodeIterations = Math.max(1, maxCodeIterations);
    }

    /**
     * A permissive-but-safe default: file writes, local git commits and model calls are pre-approved
     * so the run is autonomous, but pushing to a remote stays disabled (per current scope).
     */
    public static ApprovalPolicy autonomousDefault() {
        return new ApprovalPolicy(true, true, false, true, 3, 4);
    }

    public static ApprovalPolicy fromEnv() {
        return new ApprovalPolicy(
                envFlag("APPROVE_FILE_WRITES", true),
                envFlag("APPROVE_GIT_COMMIT", true),
                envFlag("APPROVE_GIT_PUSH", false),
                envFlag("APPROVE_MODEL_CALLS", true),
                envInt("MAX_DESIGN_ITERATIONS", 3),
                envInt("MAX_CODE_ITERATIONS", 4));
    }

    private static boolean envFlag(String key, boolean def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : Boolean.parseBoolean(v);
    }

    private static int envInt(String key, int def) {
        String v = System.getenv(key);
        try {
            return (v == null || v.isBlank()) ? def : Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean autoApproveFileWrites() { return autoApproveFileWrites; }
    public boolean autoApproveGitCommit() { return autoApproveGitCommit; }
    public boolean autoApproveGitPush() { return autoApproveGitPush; }
    public boolean autoApproveModelCalls() { return autoApproveModelCalls; }
    public int maxDesignIterations() { return maxDesignIterations; }
    public int maxCodeIterations() { return maxCodeIterations; }
}
