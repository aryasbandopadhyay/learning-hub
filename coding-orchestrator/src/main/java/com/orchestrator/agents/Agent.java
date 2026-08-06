package com.orchestrator.agents;

/**
 * A worker in the master–worker architecture. Each agent has a stable identity and a single
 * {@link #act} capability. Implementations may be LLM-backed ({@link LlmAgent}) or deterministic
 * fakes (used in tests), which keeps the orchestrator graph testable without live model calls.
 */
public interface Agent {

    String name();

    String description();

    /**
     * Performs the agent's specialized task.
     *
     * @param input the full prompt/context for this step
     * @return the agent's textual output
     */
    String act(String input);
}
