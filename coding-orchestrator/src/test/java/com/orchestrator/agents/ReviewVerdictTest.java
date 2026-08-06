package com.orchestrator.agents;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewVerdictTest {

    @Test
    void parsesExplicitApproved() {
        assertEquals(ReviewVerdict.APPROVED,
                ReviewVerdict.parse("VERDICT: APPROVED\nGreat design."));
    }

    @Test
    void parsesExplicitRevise() {
        assertEquals(ReviewVerdict.REVISE,
                ReviewVerdict.parse("VERDICT: REVISE\nFix the DIP violation."));
    }

    @Test
    void nullDefaultsToRevise() {
        assertEquals(ReviewVerdict.REVISE, ReviewVerdict.parse(null));
    }

    @Test
    void approvedButAlsoRequestingChangesIsRevise() {
        assertEquals(ReviewVerdict.REVISE,
                ReviewVerdict.parse("Not APPROVED yet. CHANGES REQUESTED: rename class."));
    }

    @Test
    void caseInsensitiveHeuristicApproved() {
        assertEquals(ReviewVerdict.APPROVED, ReviewVerdict.parse("Everything looks approved to me"));
    }
}
