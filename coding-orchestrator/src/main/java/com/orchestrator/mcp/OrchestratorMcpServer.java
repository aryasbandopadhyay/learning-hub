package com.orchestrator.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.agents.Agent;
import com.orchestrator.graph.Orchestrator;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Exposes the whole orchestrator as an MCP server over stdio, and additionally surfaces every
 * individual worker agent as its own MCP tool (so "each agent has an MCP" and the orchestrator
 * itself is an MCP). External MCP clients can call the full pipeline or any single agent.
 */
public final class OrchestratorMcpServer {

    private final Orchestrator orchestrator;

    public OrchestratorMcpServer(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    /** Builds (but does not close) the stdio MCP server with all tools registered. */
    public McpSyncServer start() {
        StdioServerTransportProvider transport = new StdioServerTransportProvider(new ObjectMapper());
        return McpServer.sync(transport)
                .serverInfo("coding-orchestrator", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder().tools(true).build())
                .tools(buildTools())
                .build();
    }

    List<SyncToolSpecification> buildTools() {
        List<SyncToolSpecification> tools = new ArrayList<>();

        // The full deterministic pipeline.
        tools.add(tool(
                "orchestrate_task",
                "Run the full design -> review -> code -> review -> tests pipeline for a coding task.",
                stringArgSchema("task", "The coding task to orchestrate."),
                args -> orchestrator.runAndReport(str(args, "task"))));

        // Each worker agent, individually invokable as its own MCP tool.
        registerAgent(tools, orchestrator.team().designer(),
                "Produce a software design for the given task.");
        registerAgent(tools, orchestrator.team().designReviewer(),
                "Review a design against SOLID principles and design patterns.");
        registerAgent(tools, orchestrator.team().implementer(),
                "Implement the approved design as code.");
        registerAgent(tools, orchestrator.team().codeReviewer(),
                "Review code against SOLID principles and design patterns.");
        registerAgent(tools, orchestrator.team().tester(),
                "Write unit tests for the implemented solution.");

        return tools;
    }

    private void registerAgent(List<SyncToolSpecification> tools, Agent agent, String description) {
        tools.add(tool(
                agent.name(),
                description,
                stringArgSchema("input", "Context/prompt for the " + agent.name() + " agent."),
                args -> agent.act(str(args, "input"))));
    }

    private SyncToolSpecification tool(String name, String description, String schema,
                                       java.util.function.Function<Map<String, Object>, String> handler) {
        McpSchema.Tool tool = new McpSchema.Tool(name, description, schema);
        BiFunction<io.modelcontextprotocol.server.McpSyncServerExchange, Map<String, Object>, McpSchema.CallToolResult>
                call = (exchange, args) -> {
            try {
                return new McpSchema.CallToolResult(handler.apply(args), false);
            } catch (RuntimeException e) {
                return new McpSchema.CallToolResult("error: " + e.getMessage(), true);
            }
        };
        return new SyncToolSpecification(tool, call);
    }

    private static String stringArgSchema(String argName, String argDescription) {
        return """
                {
                  "type": "object",
                  "properties": {
                    "%s": { "type": "string", "description": "%s" }
                  },
                  "required": ["%s"]
                }
                """.formatted(argName, argDescription, argName);
    }

    private static String str(Map<String, Object> args, String key) {
        Object v = args == null ? null : args.get(key);
        return v == null ? "" : String.valueOf(v);
    }
}
