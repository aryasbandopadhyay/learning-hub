package com.orchestrator.tools;

/** Immutable outcome of a {@link Tool} execution. */
public final class ToolResult {

    private final boolean success;
    private final String output;

    private ToolResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    public static ToolResult ok(String output) {
        return new ToolResult(true, output);
    }

    public static ToolResult error(String output) {
        return new ToolResult(false, output);
    }

    public boolean success() { return success; }

    public String output() { return output; }

    @Override
    public String toString() {
        return (success ? "OK: " : "ERROR: ") + output;
    }
}
