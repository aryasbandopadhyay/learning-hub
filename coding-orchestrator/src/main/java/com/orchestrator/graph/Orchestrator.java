package com.orchestrator.graph;

import com.orchestrator.agents.AgentTeam;
import com.orchestrator.approval.ApprovalPolicy;
import com.orchestrator.config.OrchestratorConfig;
import com.orchestrator.model.ModelFactory;
import com.orchestrator.tools.FileSystemTool;
import com.orchestrator.tools.GitTool;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * High-level facade over the master graph. Owns the deterministic tools and the (live or injected)
 * worker team, compiles the graph once, and runs tasks end-to-end.
 */
public final class Orchestrator {

    private final AgentTeam team;
    private final CompiledGraph<OrchestratorState> graph;

    public Orchestrator(AgentTeam team, OrchestratorConfig config, ApprovalPolicy policy) {
        this.team = team;
        Path workspace = Path.of(config.workspaceDir());
        FileSystemTool fs = new FileSystemTool(workspace, policy);
        GitTool git = new GitTool(workspace, policy);
        try {
            this.graph = new OrchestratorGraph(team, fs, git, policy).compile();
        } catch (GraphStateException e) {
            throw new IllegalStateException("failed to compile orchestrator graph", e);
        }
    }

    /** Builds a fully live orchestrator from environment configuration. */
    public static Orchestrator fromEnv() {
        OrchestratorConfig cfg = OrchestratorConfig.fromEnv();
        ApprovalPolicy policy = ApprovalPolicy.fromEnv();
        AgentTeam team = ModelFactory.liveTeam(cfg, policy);
        return new Orchestrator(team, cfg, policy);
    }

    public AgentTeam team() {
        return team;
    }

    /** Runs the full design → review → code → review → tests pipeline for a task. */
    public OrchestratorState run(String task) {
        return graph.invoke(Map.of(OrchestratorState.TASK, task))
                .orElseThrow(() -> new IllegalStateException("orchestrator produced no final state"));
    }

    /** Convenience: run and render a compact textual report. */
    public String runAndReport(String task) {
        OrchestratorState s = run(task);
        StringBuilder sb = new StringBuilder();
        sb.append("STATUS: ").append(s.<String>value(OrchestratorState.STATUS).orElse("unknown")).append('\n');
        sb.append("DESIGN ITERATIONS: ").append(s.designIter()).append('\n');
        sb.append("CODE ITERATIONS: ").append(s.codeIter()).append('\n');
        sb.append("\n--- TRACE ---\n");
        for (String entry : s.log()) {
            sb.append("  • ").append(entry).append('\n');
        }
        sb.append("\n--- DESIGN ---\n").append(s.design());
        sb.append("\n\n--- CODE ---\n").append(s.code());
        sb.append("\n\n--- TESTS ---\n").append(s.tests());
        return sb.toString();
    }

    public List<String> trace(OrchestratorState s) {
        return s.log();
    }
}
