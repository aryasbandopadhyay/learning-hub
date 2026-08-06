package com.orchestrator.approval;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApprovalPolicyTest {

    @Test
    void autonomousDefaultPreApprovesLocalWorkButNotPush() {
        ApprovalPolicy p = ApprovalPolicy.autonomousDefault();
        assertTrue(p.autoApproveFileWrites());
        assertTrue(p.autoApproveGitCommit());
        assertTrue(p.autoApproveModelCalls());
        assertFalse(p.autoApproveGitPush(), "push must stay disabled by default");
    }

    @Test
    void iterationCapsAreAtLeastOne() {
        ApprovalPolicy p = new ApprovalPolicy(true, true, false, true, 0, -5);
        assertEquals(1, p.maxDesignIterations());
        assertEquals(1, p.maxCodeIterations());
    }
}
