package com.orchestrator.agents;

/**
 * The set of workers the master delegates to. Bundling them behind an explicit type lets the graph
 * be constructed with either live {@link LlmAgent}s or test doubles, without touching routing logic.
 */
public final class AgentTeam {

    private final Agent designer;
    private final Agent designReviewer;
    private final Agent implementer;
    private final Agent codeReviewer;
    private final Agent tester;

    public AgentTeam(Agent designer,
                     Agent designReviewer,
                     Agent implementer,
                     Agent codeReviewer,
                     Agent tester) {
        this.designer = designer;
        this.designReviewer = designReviewer;
        this.implementer = implementer;
        this.codeReviewer = codeReviewer;
        this.tester = tester;
    }

    public Agent designer() { return designer; }
    public Agent designReviewer() { return designReviewer; }
    public Agent implementer() { return implementer; }
    public Agent codeReviewer() { return codeReviewer; }
    public Agent tester() { return tester; }
}
