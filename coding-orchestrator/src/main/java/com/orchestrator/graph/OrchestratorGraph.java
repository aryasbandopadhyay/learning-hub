package com.orchestrator.graph;

import com.orchestrator.agents.AgentTeam;
import com.orchestrator.agents.ReviewVerdict;
import com.orchestrator.approval.ApprovalPolicy;
import com.orchestrator.tools.FileSystemTool;
import com.orchestrator.tools.GitTool;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;

import java.util.List;
import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * The MASTER of the master–worker architecture: a deterministic, compiled {@link StateGraph} that
 * delegates each pipeline step to a specialized worker agent and routes purely on explicit control
 * signals in the shared state (approval flags + capped iteration counters). No routing decision is
 * left to an LLM, so the topology is reproducible.
 *
 * <pre>
 *   START -> design -> scrutinize_design --approved--> implement -> scrutinize_code --approved--> write_tests -> END
 *                          |                                |
 *                          +--revise--> design             +--revise--> implement
 * </pre>
 */
public final class OrchestratorGraph {

    private final AgentTeam team;
    private final FileSystemTool fs;
    private final GitTool git;
    private final ApprovalPolicy policy;

    public OrchestratorGraph(AgentTeam team, FileSystemTool fs, GitTool git, ApprovalPolicy policy) {
        this.team = team;
        this.fs = fs;
        this.git = git;
        this.policy = policy;
    }

    public CompiledGraph<OrchestratorState> compile() throws GraphStateException {
        StateGraph<OrchestratorState> graph = new StateGraph<>(OrchestratorState.SCHEMA, OrchestratorState::new)
                .addNode("design", node_async(this::designNode))
                .addNode("scrutinize_design", node_async(this::scrutinizeDesignNode))
                .addNode("implement", node_async(this::implementNode))
                .addNode("scrutinize_code", node_async(this::scrutinizeCodeNode))
                .addNode("write_tests", node_async(this::writeTestsNode))
                .addEdge(START, "design")
                .addEdge("design", "scrutinize_design")
                .addConditionalEdges("scrutinize_design",
                        edge_async(this::routeAfterDesignReview),
                        Map.of("approved", "implement", "revise", "design"))
                .addEdge("implement", "scrutinize_code")
                .addConditionalEdges("scrutinize_code",
                        edge_async(this::routeAfterCodeReview),
                        Map.of("approved", "write_tests", "revise", "implement"))
                .addEdge("write_tests", END);
        return graph.compile();
    }

    // ----- Worker nodes -------------------------------------------------------------------------

    private Map<String, Object> designNode(OrchestratorState s) {
        String feedback = s.designReview();
        String context = "TASK:\n" + s.task()
                + (feedback.isBlank() ? "" : "\n\nPREVIOUS REVIEW FEEDBACK TO ADDRESS:\n" + feedback);
        String design = team.designer().act(context);
        int iter = s.designIter() + 1;
        return Map.of(
                OrchestratorState.DESIGN, design,
                OrchestratorState.DESIGN_ITER, iter,
                OrchestratorState.STATUS, "designed",
                OrchestratorState.LOG, "design: produced design (iteration " + iter + ")");
    }

    private Map<String, Object> scrutinizeDesignNode(OrchestratorState s) {
        String context = "TASK:\n" + s.task() + "\n\nDESIGN UNDER REVIEW:\n" + s.design();
        String review = team.designReviewer().act(context);
        boolean approved = ReviewVerdict.parse(review) == ReviewVerdict.APPROVED;
        return Map.of(
                OrchestratorState.DESIGN_REVIEW, approved ? "" : review,
                OrchestratorState.DESIGN_APPROVED, approved,
                OrchestratorState.STATUS, approved ? "design_approved" : "design_revise",
                OrchestratorState.LOG, "scrutinize_design: " + (approved ? "APPROVED" : "REVISE"));
    }

    private String routeAfterDesignReview(OrchestratorState s) {
        boolean capReached = s.designIter() >= policy.maxDesignIterations();
        return (s.designApproved() || capReached) ? "approved" : "revise";
    }

    private Map<String, Object> implementNode(OrchestratorState s) {
        String feedback = s.codeReview();
        String context = "APPROVED DESIGN:\n" + s.design() + "\n\nTASK:\n" + s.task()
                + (feedback.isBlank() ? "" : "\n\nPREVIOUS CODE REVIEW FEEDBACK TO ADDRESS:\n" + feedback);
        String code = team.implementer().act(context);
        int iter = s.codeIter() + 1;
        return Map.of(
                OrchestratorState.CODE, code,
                OrchestratorState.CODE_ITER, iter,
                OrchestratorState.STATUS, "implemented",
                OrchestratorState.LOG, "implement: produced code (iteration " + iter + ")");
    }

    private Map<String, Object> scrutinizeCodeNode(OrchestratorState s) {
        String context = "DESIGN:\n" + s.design() + "\n\nCODE UNDER REVIEW:\n" + s.code();
        String review = team.codeReviewer().act(context);
        boolean approved = ReviewVerdict.parse(review) == ReviewVerdict.APPROVED;
        return Map.of(
                OrchestratorState.CODE_REVIEW, approved ? "" : review,
                OrchestratorState.CODE_APPROVED, approved,
                OrchestratorState.STATUS, approved ? "code_approved" : "code_revise",
                OrchestratorState.LOG, "scrutinize_code: " + (approved ? "APPROVED (harmony)" : "REVISE"));
    }

    private String routeAfterCodeReview(OrchestratorState s) {
        boolean capReached = s.codeIter() >= policy.maxCodeIterations();
        return (s.codeApproved() || capReached) ? "approved" : "revise";
    }

    private Map<String, Object> writeTestsNode(OrchestratorState s) {
        String context = "SOLUTION CODE:\n" + s.code();
        String tests = team.tester().act(context);

        List<String> written = ArtifactWriter.write(fs, s.code(), "solution/CODE.txt");
        written.addAll(ArtifactWriter.write(fs, tests, "solution/TESTS.txt"));
        fs.writeFile("solution/DESIGN.md", s.design());

        // Persist the work with a local commit (push is policy-gated and out of scope for now).
        git.init();
        var commit = git.commitAll("orchestrator: design, implementation and unit tests");

        return Map.of(
                OrchestratorState.TESTS, tests,
                OrchestratorState.STATUS, "completed",
                OrchestratorState.LOG, "write_tests: wrote " + written.size()
                        + " artifact(s); git " + (commit.success() ? "committed" : "skipped/failed"));
    }
}
