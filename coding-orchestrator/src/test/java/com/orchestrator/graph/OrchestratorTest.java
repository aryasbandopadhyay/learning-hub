package com.orchestrator.graph;

import com.orchestrator.agents.AgentTeam;
import com.orchestrator.approval.ApprovalPolicy;
import com.orchestrator.config.OrchestratorConfig;
import com.orchestrator.support.FakeAgents;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrchestratorTest {

    @TempDir
    Path workspace;

    private OrchestratorConfig config() {
        return new OrchestratorConfig(null, "gpt-4o", null,
                "claude-3-5-sonnet", workspace.toString());
    }

    @Test
    void runsFullPipelineToCompletionAndWritesArtifacts() {
        AgentTeam team = FakeAgents.convergingTeam(1, 1); // one design revise, one code revise
        Orchestrator orchestrator =
                new Orchestrator(team, config(), ApprovalPolicy.autonomousDefault());

        OrchestratorState s = orchestrator.run("Build a small calculator");

        assertEquals("completed", s.<String>value(OrchestratorState.STATUS).orElse(""));
        assertEquals(2, s.designIter(), "1 revise + 1 approved => 2 design iterations");
        assertEquals(2, s.codeIter(), "1 revise + 1 approved => 2 code iterations");

        assertTrue(Files.exists(workspace.resolve("solution/DESIGN.md")));
        assertTrue(Files.exists(workspace.resolve("src/main/java/app/Calc.java")));
        assertTrue(Files.exists(workspace.resolve("src/test/java/app/CalcTest.java")));
    }

    @Test
    void respectsDesignIterationCapWhenReviewerNeverApproves() {
        // Reviewer always revises; master must still terminate at the cap and proceed.
        AgentTeam team = new AgentTeam(
                FakeAgents.constant("design", "d"),
                FakeAgents.reviewer("scrutinize_design", 99),
                FakeAgents.constant("implement", "code"),
                FakeAgents.reviewer("scrutinize_code", 0),
                FakeAgents.constant("write_tests", "tests"));
        ApprovalPolicy policy = new ApprovalPolicy(true, true, false, true, 2, 3);
        Orchestrator orchestrator = new Orchestrator(team, config(), policy);

        OrchestratorState s = orchestrator.run("anything");

        assertEquals("completed", s.<String>value(OrchestratorState.STATUS).orElse(""));
        assertEquals(2, s.designIter(), "design loop stops at the cap");
    }
}
