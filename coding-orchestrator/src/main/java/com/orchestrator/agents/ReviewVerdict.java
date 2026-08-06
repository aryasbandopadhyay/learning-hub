package com.orchestrator.agents;

/**
 * Outcome of a scrutiny step. Reviewers are instructed to emit a line beginning with
 * {@code VERDICT: APPROVED} or {@code VERDICT: REVISE}; {@link #parse} extracts it deterministically
 * so the master state-machine can route without any further LLM interpretation.
 */
public enum ReviewVerdict {
    APPROVED,
    REVISE;

    public static ReviewVerdict parse(String reviewText) {
        if (reviewText == null) {
            return REVISE;
        }
        String upper = reviewText.toUpperCase();
        int idx = upper.indexOf("VERDICT:");
        if (idx >= 0) {
            String after = upper.substring(idx + "VERDICT:".length()).stripLeading();
            if (after.startsWith("APPROVED")) {
                return APPROVED;
            }
            if (after.startsWith("REVISE")) {
                return REVISE;
            }
        }
        // Fallback heuristic: only APPROVED when explicitly approved and not asking for changes.
        boolean approved = upper.contains("APPROVED");
        boolean revise = upper.contains("REVISE") || upper.contains("CHANGES REQUESTED");
        return (approved && !revise) ? APPROVED : REVISE;
    }
}
