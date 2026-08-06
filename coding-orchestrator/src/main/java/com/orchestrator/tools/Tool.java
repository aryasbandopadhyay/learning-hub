package com.orchestrator.tools;

import java.util.Map;

/**
 * A deterministic tool: a plain Java "script" with a declared name/description and a well-defined,
 * side-effecting {@link #execute} entry point. Tools contain <em>no</em> LLM logic — they are the
 * reliable, reproducible hands of the agents.
 */
public interface Tool {

    String name();

    String description();

    /**
     * Executes the tool.
     *
     * @param args named arguments
     * @return a structured result
     */
    ToolResult execute(Map<String, Object> args);
}
