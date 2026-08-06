package com.orchestrator.mcp;

import com.orchestrator.approval.ApprovalPolicy;
import com.orchestrator.config.OrchestratorConfig;
import com.orchestrator.graph.Orchestrator;
import com.orchestrator.support.FakeAgents;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrchestratorMcpServerTest {

    @TempDir
    Path workspace;

    @Test
    void exposesOrchestratorAndEachAgentAsTools() {
        OrchestratorConfig cfg = new OrchestratorConfig(null, "gpt-4o", null,
                "claude", workspace.toString());
        Orchestrator orchestrator = new Orchestrator(
                FakeAgents.convergingTeam(0, 0), cfg, ApprovalPolicy.autonomousDefault());

        List<SyncToolSpecification> tools = new OrchestratorMcpServer(orchestrator).buildTools();
        Set<String> names = tools.stream().map(t -> t.tool().name()).collect(Collectors.toSet());

        assertEquals(6, tools.size());
        assertTrue(names.containsAll(Set.of(
                "orchestrate_task", "design", "scrutinize_design",
                "implement", "scrutinize_code", "write_tests")));
    }

    @Test
    void singleAgentToolInvokesThatAgent() {
        OrchestratorConfig cfg = new OrchestratorConfig(null, "m", null, "m", workspace.toString());
        Orchestrator orchestrator = new Orchestrator(
                FakeAgents.convergingTeam(0, 0), cfg, ApprovalPolicy.autonomousDefault());

        SyncToolSpecification design = new OrchestratorMcpServer(orchestrator).buildTools().stream()
                .filter(t -> t.tool().name().equals("design"))
                .findFirst().orElseThrow();

        var result = design.call().apply(null, java.util.Map.of("input", "some task"));
        assertTrue(result.content().toString().contains("Strategy"));
    }
}
