package com.orchestrator.config;

/**
 * Immutable runtime configuration for the orchestrator, resolved once from environment variables.
 *
 * <p>Model providers are intentionally split so scrutiny uses a <em>different</em> provider than
 * implementation, reducing self-review bias:
 * <ul>
 *   <li>Implementation side (design / code / tests): OpenAI.</li>
 *   <li>Scrutiny side (design review / code review): Anthropic.</li>
 * </ul>
 */
public final class OrchestratorConfig {

    private final String openAiApiKey;
    private final String openAiModel;
    private final String anthropicApiKey;
    private final String anthropicModel;
    private final String workspaceDir;

    public OrchestratorConfig(String openAiApiKey,
                              String openAiModel,
                              String anthropicApiKey,
                              String anthropicModel,
                              String workspaceDir) {
        this.openAiApiKey = openAiApiKey;
        this.openAiModel = openAiModel;
        this.anthropicApiKey = anthropicApiKey;
        this.anthropicModel = anthropicModel;
        this.workspaceDir = workspaceDir;
    }

    /** Builds a config from environment variables, applying sensible defaults for model names. */
    public static OrchestratorConfig fromEnv() {
        return new OrchestratorConfig(
                System.getenv("OPENAI_API_KEY"),
                envOrDefault("OPENAI_MODEL", "gpt-4o"),
                System.getenv("ANTHROPIC_API_KEY"),
                envOrDefault("ANTHROPIC_MODEL", "claude-3-5-sonnet-20241022"),
                envOrDefault("ORCHESTRATOR_WORKSPACE",
                        System.getProperty("java.io.tmpdir") + "/orchestrator-workspace"));
    }

    private static String envOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    /** True when both providers have credentials and real LLM calls can be made. */
    public boolean hasLiveCredentials() {
        return isSet(openAiApiKey) && isSet(anthropicApiKey);
    }

    private static boolean isSet(String s) {
        return s != null && !s.isBlank();
    }

    public String openAiApiKey() { return openAiApiKey; }
    public String openAiModel() { return openAiModel; }
    public String anthropicApiKey() { return anthropicApiKey; }
    public String anthropicModel() { return anthropicModel; }
    public String workspaceDir() { return workspaceDir; }
}
