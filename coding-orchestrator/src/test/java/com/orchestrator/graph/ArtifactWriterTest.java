package com.orchestrator.graph;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtifactWriterTest {

    @Test
    void parsesMultipleFileBlocks() {
        String content = """
                === FILE: src/main/java/A.java ===
                package p;
                class A {}
                === FILE: src/test/java/ATest.java ===
                package p;
                class ATest {}
                """;
        Map<String, String> files = ArtifactWriter.parse(content);
        assertEquals(2, files.size());
        assertTrue(files.containsKey("src/main/java/A.java"));
        assertTrue(files.get("src/main/java/A.java").contains("class A"));
        assertTrue(files.get("src/test/java/ATest.java").contains("class ATest"));
    }

    @Test
    void stripsCodeFences() {
        String content = """
                === FILE: A.java ===
                ```java
                class A {}
                ```
                """;
        Map<String, String> files = ArtifactWriter.parse(content);
        String body = files.get("A.java");
        assertTrue(body.startsWith("class A"), "fence should be stripped: " + body);
    }

    @Test
    void emptyWhenNoBlocks() {
        assertTrue(ArtifactWriter.parse("just prose, no file markers").isEmpty());
    }
}
