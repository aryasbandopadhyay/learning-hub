package com.orchestrator.support;

import com.orchestrator.agents.Agent;
import com.orchestrator.agents.AgentTeam;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Deterministic in-memory agents for tests. No network / LLM calls are ever made, satisfying the
 * "no live calls in tests" requirement while still exercising the full master graph and routing.
 */
public final class FakeAgents {

    private FakeAgents() {
    }

    /** A fixed-output agent. */
    public static Agent constant(String name, String output) {
        return function(name, in -> output);
    }

    /** An agent whose output is computed from its input. */
    public static Agent function(String name, Function<String, String> fn) {
        return new Agent() {
            @Override public String name() { return name; }
            @Override public String description() { return "fake:" + name; }
            @Override public String act(String input) { return fn.apply(input); }
        };
    }

    /**
     * A reviewer that emits {@code VERDICT: REVISE} for its first {@code reviseTimes} calls and
     * {@code VERDICT: APPROVED} afterwards — lets tests exercise the revise loop then convergence.
     */
    public static Agent reviewer(String name, int reviseTimes) {
        AtomicInteger calls = new AtomicInteger();
        return function(name, in -> {
            int n = calls.incrementAndGet();
            return n <= reviseTimes
                    ? "VERDICT: REVISE\nPlease address items A and B."
                    : "VERDICT: APPROVED\nLooks good.";
        });
    }

    /** A team that converges after the given number of design/code revisions. */
    public static AgentTeam convergingTeam(int designRevises, int codeRevises) {
        return new AgentTeam(
                constant("design", "Design v1: layered, uses Strategy + Factory."),
                reviewer("scrutinize_design", designRevises),
                constant("implement",
                        "=== FILE: src/main/java/app/Calc.java ===\n"
                                + "package app;\npublic class Calc { public int add(int a,int b){return a+b;} }\n"),
                reviewer("scrutinize_code", codeRevises),
                constant("write_tests",
                        "=== FILE: src/test/java/app/CalcTest.java ===\n"
                                + "package app;\nimport org.junit.jupiter.api.Test;\n"
                                + "import static org.junit.jupiter.api.Assertions.*;\n"
                                + "class CalcTest { @Test void adds(){ assertEquals(3, new Calc().add(1,2)); } }\n"));
    }
}
