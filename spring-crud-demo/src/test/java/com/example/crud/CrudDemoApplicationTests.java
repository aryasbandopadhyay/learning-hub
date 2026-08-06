package com.example.crud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ============================================================================================
 * CrudDemoApplicationTests — the "smoke test": does the whole application context start?
 * ============================================================================================
 *
 * <h3>{@code @SpringBootTest}</h3>
 * Boots the FULL Spring application context (all beans, wiring, JPA, H2) for the test. If any bean
 * is misconfigured or a dependency can't be injected, {@code contextLoads()} fails. It's the
 * fastest way to catch wiring/config mistakes.
 */
@SpringBootTest
class CrudDemoApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty: success = the context started without errors.
    }
}
