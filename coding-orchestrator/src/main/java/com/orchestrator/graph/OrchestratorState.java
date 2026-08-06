package com.orchestrator.graph;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shared state passed between nodes of the master graph. Keys model the pipeline artifacts and the
 * deterministic control signals (approvals, iteration counters) the master routes on.
 */
public final class OrchestratorState extends AgentState {

    public static final String TASK = "task";
    public static final String DESIGN = "design";
    public static final String DESIGN_REVIEW = "design_review";
    public static final String DESIGN_APPROVED = "design_approved";
    public static final String DESIGN_ITER = "design_iter";
    public static final String CODE = "code";
    public static final String CODE_REVIEW = "code_review";
    public static final String CODE_APPROVED = "code_approved";
    public static final String CODE_ITER = "code_iter";
    public static final String TESTS = "tests";
    public static final String STATUS = "status";
    public static final String LOG = "log";

    /** State schema. {@code log} accumulates a trace; everything else is last-writer-wins. */
    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            LOG, Channels.appender(ArrayList::new));

    public OrchestratorState(Map<String, Object> initData) {
        super(initData);
    }

    public String task() {
        return this.<String>value(TASK).orElse("");
    }

    public String design() {
        return this.<String>value(DESIGN).orElse("");
    }

    public String code() {
        return this.<String>value(CODE).orElse("");
    }

    public String tests() {
        return this.<String>value(TESTS).orElse("");
    }

    public boolean designApproved() {
        return this.<Boolean>value(DESIGN_APPROVED).orElse(false);
    }

    public boolean codeApproved() {
        return this.<Boolean>value(CODE_APPROVED).orElse(false);
    }

    public int designIter() {
        return this.<Integer>value(DESIGN_ITER).orElse(0);
    }

    public int codeIter() {
        return this.<Integer>value(CODE_ITER).orElse(0);
    }

    public String designReview() {
        return this.<String>value(DESIGN_REVIEW).orElse("");
    }

    public String codeReview() {
        return this.<String>value(CODE_REVIEW).orElse("");
    }

    @SuppressWarnings("unchecked")
    public List<String> log() {
        return this.<List<String>>value(LOG).orElse(List.of());
    }
}
