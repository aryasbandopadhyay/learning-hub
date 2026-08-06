package com.orchestrator.tools;

import com.orchestrator.approval.ApprovalPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSystemToolTest {

    private static final ApprovalPolicy APPROVED = ApprovalPolicy.autonomousDefault();
    private static final ApprovalPolicy DENIED =
            new ApprovalPolicy(false, false, false, true, 3, 4);

    @TempDir
    Path tmp;

    @Test
    void writesAndReadsBack() {
        FileSystemTool fs = new FileSystemTool(tmp, APPROVED);
        assertTrue(fs.writeFile("a/b/File.java", "hello").success());
        ToolResult read = fs.readFile("a/b/File.java");
        assertTrue(read.success());
        assertEquals("hello", read.output());
    }

    @Test
    void rejectsPathEscape() {
        FileSystemTool fs = new FileSystemTool(tmp, APPROVED);
        ToolResult r = fs.writeFile("../escape.txt", "x");
        assertFalse(r.success());
        assertTrue(r.output().contains("escapes workspace"));
    }

    @Test
    void respectsWriteApprovalGate() {
        FileSystemTool fs = new FileSystemTool(tmp, DENIED);
        ToolResult r = fs.writeFile("x.txt", "x");
        assertFalse(r.success());
        assertTrue(r.output().contains("not approved"));
    }
}
