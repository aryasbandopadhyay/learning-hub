package com.orchestrator;

import com.orchestrator.approval.ApprovalPolicy;
import com.orchestrator.config.OrchestratorConfig;
import com.orchestrator.graph.Orchestrator;
import com.orchestrator.mcp.OrchestratorMcpServer;
import io.modelcontextprotocol.server.McpSyncServer;

/**
 * Entry point with two modes:
 * <ul>
 *   <li>{@code mcp} (default) — start the stdio MCP server exposing the orchestrator and each agent.</li>
 *   <li>{@code run "<task>"} — run the full pipeline once for a task and print a report.</li>
 * </ul>
 *
 * <p>Approvals are front-loaded from the environment ({@link ApprovalPolicy#fromEnv()}), so no
 * interactive prompts occur during a run.
 */
public final class OrchestratorApp {

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "mcp";

        OrchestratorConfig cfg = OrchestratorConfig.fromEnv();
        if (!cfg.hasLiveCredentials()) {
            System.err.println("[warn] OPENAI_API_KEY and/or ANTHROPIC_API_KEY not set — "
                    + "live model calls will fail. Set both to run the pipeline.");
        }

        switch (mode) {
            case "run" -> runOnce(args);
            case "mcp" -> serveMcp();
            default -> {
                System.err.println("Usage: orchestrator [mcp | run \"<task>\"]");
                System.exit(2);
            }
        }
    }

    private static void runOnce(String[] args) {
        if (args.length < 2 || args[1].isBlank()) {
            System.err.println("Usage: orchestrator run \"<task description>\"");
            System.exit(2);
            return;
        }
        Orchestrator orchestrator = Orchestrator.fromEnv();
        System.out.println(orchestrator.runAndReport(args[1]));
    }

    private static void serveMcp() {
        Orchestrator orchestrator = Orchestrator.fromEnv();
        McpSyncServer server = new OrchestratorMcpServer(orchestrator).start();
        System.err.println("[info] coding-orchestrator MCP server started on stdio.");
        Runtime.getRuntime().addShutdownHook(new Thread(server::closeGracefully));
        // Keep the process alive; the stdio transport drives request handling.
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
