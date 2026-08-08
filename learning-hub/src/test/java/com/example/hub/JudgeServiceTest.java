package com.example.hub;

import com.example.hub.config.JudgeProperties;
import com.example.hub.service.JudgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests for {@link JudgeService}.
 *
 * <p>Metadata tests are hermetic (a hand-written manifest in a {@link TempDir}). The end-to-end
 * run test actually shells out to the real {@code judge/runner.py}, so it is skipped (not failed)
 * when a {@code python} interpreter isn't available on the build machine.
 */
class JudgeServiceTest {

    @TempDir
    Path manifests;

    private JudgeService serviceWith(Path judgeDir) {
        JudgeProperties props = new JudgeProperties(
                true, "python", judgeDir.toString(), manifests.toString(), 20);
        return new JudgeService(props);
    }

    /** Path to the real judge/ dir (contains runner.py), relative to the module working dir. */
    private Path realJudgeDir() {
        return Paths.get(System.getProperty("user.dir")).resolve("judge").toAbsolutePath().normalize();
    }

    private void writeTwoSumManifest() throws IOException {
        Path dir = manifests.resolve("arrays-hashing");
        Files.createDirectories(dir);
        String json = """
            {
              "id": "arrays-hashing/two-sum",
              "title": "Two Sum",
              "difficulty": "Easy",
              "topic": "arrays-hashing",
              "shape": "function",
              "entry": "twoSum",
              "className": "Solution",
              "compareMode": "exact",
              "starterCode": "class Solution:\\n    def twoSum(self, nums, target):\\n        pass\\n",
              "complexity": {"supported": false},
              "tests": [
                {"id": "example-1", "kind": "example", "args": [[2,7,11,15], 9], "expected": [0,1]},
                {"id": "rand-1", "kind": "random", "args": [[3,2,4], 6], "expected": [1,2]}
              ],
              "solutions": {
                "naive": "class Solution:\\n    def twoSum(self, nums, target):\\n        for i in range(len(nums)):\\n            for j in range(i+1,len(nums)):\\n                if nums[i]+nums[j]==target: return [i,j]\\n        return []\\n",
                "better": "class Solution:\\n    def twoSum(self, nums, target):\\n        seen={}\\n        for i,n in enumerate(nums):\\n            if target-n in seen: return [seen[target-n], i]\\n            seen[n]=i\\n        return []\\n",
                "optimal": "class Solution:\\n    def twoSum(self, nums, target):\\n        seen={}\\n        for i,n in enumerate(nums):\\n            if target-n in seen: return [seen[target-n], i]\\n            seen[n]=i\\n        return []\\n"
              }
            }
            """;
        Files.writeString(dir.resolve("two-sum.json"), json);
    }

    @Test
    void problemMetadataIsExposedFromManifest() throws IOException {
        writeTwoSumManifest();
        JudgeService svc = serviceWith(realJudgeDir());

        Map<String, Object> meta = svc.problem("dsa/arrays-hashing/two-sum.md");
        assertThat(meta.get("available")).isEqualTo(true);
        assertThat(meta.get("title")).isEqualTo("Two Sum");
        assertThat(meta.get("entry")).isEqualTo("twoSum");
        assertThat(meta.get("starterCode").toString()).contains("def twoSum");
        // Only the example test is exposed as a visible sample (hidden random tests are withheld).
        assertThat((List<?>) meta.get("sampleTests")).hasSize(1);
        @SuppressWarnings("unchecked")
        Map<String, Object> sols = (Map<String, Object>) meta.get("solutions");
        assertThat(sols).containsKeys("naive", "better", "optimal");
    }

    @Test
    void unknownProblemReportsUnavailable() {
        JudgeService svc = serviceWith(realJudgeDir());
        Map<String, Object> meta = svc.problem("dsa/arrays-hashing/does-not-exist.md");
        assertThat(meta.get("available")).isEqualTo(false);
    }

    // ---- index(section) — the Problems dashboard catalogue -------------------------------

    /** Write a minimal manifest (only the fields index() reads) at {@code <rel>.json}. */
    private void writeIndexManifest(String rel, String title, String difficulty, String topic) throws IOException {
        Path file = manifests.resolve(rel + ".json");
        Files.createDirectories(file.getParent());
        String json = """
            {"id":"%s","title":"%s","difficulty":"%s","topic":"%s","shape":"function",
             "entry":"solve","className":"Solution","tests":[]}
            """.formatted(rel, title, difficulty, topic);
        Files.writeString(file, json);
    }

    @Test
    void indexMapsManifestPathsAndSplitsSections() throws IOException {
        writeIndexManifest("arrays-hashing/two-sum", "Two Sum", "Easy", "arrays-hashing");
        writeIndexManifest("google/dp/edit-distance", "Edit Distance", "Hard", "google/dp");
        writeIndexManifest("faang/heap/k-closest", "K Closest", "Medium", "faang/heap");
        JudgeService svc = serviceWith(realJudgeDir());

        // Full catalogue: every manifest, sorted by topic then path.
        List<Map<String, Object>> all = svc.index(null);
        assertThat(all).hasSize(3);

        // Manifest rel path -> content path is "dsa/" + relNoExt + ".md".
        Map<String, Object> twoSum = all.stream()
                .filter(m -> "Two Sum".equals(m.get("title"))).findFirst().orElseThrow();
        assertThat(twoSum.get("path")).isEqualTo("dsa/arrays-hashing/two-sum.md");
        assertThat(twoSum.get("section")).isEqualTo("dsa");
        assertThat(twoSum.get("difficulty")).isEqualTo("Easy");

        // Section attribution follows the google/ and faang/ prefixes.
        assertThat(all.stream().filter(m -> "google".equals(m.get("section")))).hasSize(1);
        assertThat(all.stream().filter(m -> "faang".equals(m.get("section")))).hasSize(1);
        assertThat(all.stream().filter(m -> "dsa".equals(m.get("section")))).hasSize(1);
        Map<String, Object> edit = all.stream()
                .filter(m -> "Edit Distance".equals(m.get("title"))).findFirst().orElseThrow();
        assertThat(edit.get("path")).isEqualTo("dsa/google/dp/edit-distance.md");
    }

    @Test
    void siblingsOfMatchesSameSlugAcrossSections() throws IOException {
        // Same problem ("candy") in the main bank and FAANG, plus an unrelated problem.
        writeIndexManifest("greedy/09-candy", "Candy", "Hard", "greedy");
        writeIndexManifest("faang/greedy-scheduling/14-candy", "Candy", "Hard", "faang/greedy-scheduling");
        writeIndexManifest("greedy/10-task-scheduler", "Task Scheduler", "Medium", "greedy");
        JudgeService svc = serviceWith(realJudgeDir());

        List<Map<String, Object>> sibs = svc.siblingsOf("dsa/greedy/09-candy.md");
        assertThat(sibs).hasSize(2); // includes the input problem itself
        assertThat(sibs.stream().map(m -> m.get("path")))
                .containsExactlyInAnyOrder("dsa/greedy/09-candy.md",
                        "dsa/faang/greedy-scheduling/14-candy.md");
        assertThat(sibs.stream().map(m -> m.get("section")))
                .containsExactlyInAnyOrder("dsa", "faang");
    }

    @Test
    void siblingsOfReturnsJustItselfWhenNoOverlap() throws IOException {
        writeIndexManifest("greedy/10-task-scheduler", "Task Scheduler", "Medium", "greedy");
        JudgeService svc = serviceWith(realJudgeDir());
        assertThat(svc.siblingsOf("dsa/greedy/10-task-scheduler.md"))
                .singleElement()
                .satisfies(m -> assertThat(m.get("path")).isEqualTo("dsa/greedy/10-task-scheduler.md"));
    }

    @Test
    void indexFiltersToRequestedSection() throws IOException {
        writeIndexManifest("arrays-hashing/two-sum", "Two Sum", "Easy", "arrays-hashing");
        writeIndexManifest("google/dp/edit-distance", "Edit Distance", "Hard", "google/dp");
        writeIndexManifest("faang/heap/k-closest", "K Closest", "Medium", "faang/heap");
        JudgeService svc = serviceWith(realJudgeDir());

        assertThat(svc.index("google")).singleElement()
                .satisfies(m -> assertThat(m.get("title")).isEqualTo("Edit Distance"));
        assertThat(svc.index("dsa")).singleElement()
                .satisfies(m -> assertThat(m.get("section")).isEqualTo("dsa"));
        assertThat(svc.index("nope")).isEmpty();
    }

    @Test
    void runGradesACorrectSubmission() throws IOException {
        assumeTrue(pythonAvailable(), "python interpreter not available; skipping end-to-end run");
        assumeTrue(Files.isRegularFile(realJudgeDir().resolve("runner.py")),
                "judge/runner.py not found; skipping");
        writeTwoSumManifest();
        JudgeService svc = serviceWith(realJudgeDir());

        String good = "class Solution:\n"
                + "    def twoSum(self, nums, target):\n"
                + "        seen = {}\n"
                + "        for i, n in enumerate(nums):\n"
                + "            if target - n in seen:\n"
                + "                return [seen[target - n], i]\n"
                + "            seen[n] = i\n"
                + "        return []\n";

        Map<String, Object> res = svc.run("dsa/arrays-hashing/two-sum.md", good, "run");
        assertThat(res.get("ok")).isEqualTo(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) res.get("summary");
        assertThat(summary.get("allPassed")).isEqualTo(true);
    }

    @Test
    void runRejectsAWrongSubmission() throws IOException {
        assumeTrue(pythonAvailable(), "python interpreter not available; skipping end-to-end run");
        writeTwoSumManifest();
        JudgeService svc = serviceWith(realJudgeDir());

        String bad = "class Solution:\n"
                + "    def twoSum(self, nums, target):\n"
                + "        return [0, 0]\n";

        Map<String, Object> res = svc.run("dsa/arrays-hashing/two-sum.md", bad, "run");
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) res.get("summary");
        assertThat(summary.get("allPassed")).isEqualTo(false);
    }

    private static boolean pythonAvailable() {
        try {
            Process p = new ProcessBuilder("python", "--version").start();
            return p.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            return false;
        }
    }
}
