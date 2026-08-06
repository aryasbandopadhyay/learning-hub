package com.example.hub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifies the Spring application context starts and all beans wire up correctly
 * (properties bind, ContentService + ContentController construct without error).
 */
@SpringBootTest
class LearningHubApplicationTests {

    @Test
    void contextLoads() {
        // If the context fails to start, this test fails.
    }
}
