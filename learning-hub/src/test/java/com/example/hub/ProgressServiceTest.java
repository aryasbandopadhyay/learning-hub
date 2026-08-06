package com.example.hub;

import com.example.hub.config.ProgressProperties;
import com.example.hub.service.ProgressService;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProgressService}'s in-memory fallback (used whenever no Azure connection
 * string is configured — exactly the CI/local case, so these run with zero cloud dependency).
 */
class ProgressServiceTest {

    /** A ProgressService with a blank connection string => in-memory store. */
    private ProgressService inMemory() {
        return new ProgressService(new ProgressProperties(true, "", "progress"));
    }

    @Test
    void marksAndReadsBackCompletion() {
        ProgressService svc = inMemory();
        String user = "alice@example.com";

        assertThat(svc.completed(user, null)).isEmpty();

        svc.set(user, "dsa/arrays-hashing/two-sum.md", "dsa", true);
        svc.set(user, "dsa/google/dp/edit-distance.md", "google", true);

        assertThat(svc.completed(user, null))
                .containsExactlyInAnyOrder(
                        "dsa/arrays-hashing/two-sum.md",
                        "dsa/google/dp/edit-distance.md");
    }

    @Test
    void completedIsFilteredBySection() {
        ProgressService svc = inMemory();
        String user = "bob@example.com";
        svc.set(user, "dsa/arrays-hashing/two-sum.md", "dsa", true);
        svc.set(user, "dsa/google/dp/edit-distance.md", "google", true);

        assertThat(svc.completed(user, "dsa")).containsExactly("dsa/arrays-hashing/two-sum.md");
        assertThat(svc.completed(user, "google")).containsExactly("dsa/google/dp/edit-distance.md");
        assertThat(svc.completed(user, "faang")).isEmpty();
    }

    @Test
    void unmarkingRemovesTheProblem() {
        ProgressService svc = inMemory();
        String user = "carol@example.com";
        String path = "dsa/arrays-hashing/two-sum.md";

        svc.set(user, path, "dsa", true);
        assertThat(svc.completed(user, null)).contains(path);

        svc.set(user, path, "dsa", false);
        assertThat(svc.completed(user, null)).doesNotContain(path);
    }

    @Test
    void resetIsScopedToASectionOrClearsEverything() {
        ProgressService svc = inMemory();
        String user = "dave@example.com";
        svc.set(user, "dsa/a.md", "dsa", true);
        svc.set(user, "dsa/google/b.md", "google", true);
        svc.set(user, "dsa/google/c.md", "google", true);

        // Section-scoped reset only clears that section.
        int removed = svc.reset(user, "google");
        assertThat(removed).isEqualTo(2);
        assertThat(svc.completed(user, null)).containsExactly("dsa/a.md");

        // Full reset clears the rest.
        assertThat(svc.reset(user, null)).isEqualTo(1);
        assertThat(svc.completed(user, null)).isEmpty();
    }

    @Test
    void progressIsIsolatedPerUser() {
        ProgressService svc = inMemory();
        svc.set("u1@example.com", "dsa/a.md", "dsa", true);
        svc.set("u2@example.com", "dsa/b.md", "dsa", true);

        assertThat(svc.completed("u1@example.com", null)).containsExactly("dsa/a.md");
        assertThat(svc.completed("u2@example.com", null)).containsExactly("dsa/b.md");
    }

    @Test
    void nullOrBlankUserFallsBackToDefaultBucket() {
        ProgressService svc = inMemory();
        svc.set(null, "dsa/a.md", "dsa", true);
        // Both null and blank normalise to the same "default" partition.
        assertThat(svc.completed("", null)).containsExactly("dsa/a.md");
    }
}
