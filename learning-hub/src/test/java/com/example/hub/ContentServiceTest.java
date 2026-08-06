package com.example.hub;

import com.example.hub.config.ContentProperties;
import com.example.hub.model.CategoryDto;
import com.example.hub.model.FileContent;
import com.example.hub.model.TreeNode;
import com.example.hub.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ContentService} — built against a throwaway {@link TempDir} content root
 * so they are hermetic (independent of the real Projects folder).
 */
class ContentServiceTest {

    @TempDir
    Path root;

    private ContentService service;

    @BeforeEach
    void setUp() throws IOException {
        // Layout under the temp root:
        //   lld/parking-lot/README.md
        //   lld/parking-lot/src/Main.java
        //   lld/parking-lot/target/ignored.class   (excluded dir)
        //   lld/parking-lot/notes.bin               (disallowed extension)
        //   secret/private.md                       (NOT in any category path)
        Path pl = root.resolve("lld/parking-lot");
        Files.createDirectories(pl.resolve("src"));
        Files.createDirectories(pl.resolve("target"));
        Files.writeString(pl.resolve("README.md"), "# Parking Lot\n\nHello.");
        Files.writeString(pl.resolve("src/Main.java"), "class Main {}");
        Files.writeString(pl.resolve("target/ignored.class"), "binary");
        Files.writeString(pl.resolve("notes.bin"), "nope");
        Files.createDirectories(root.resolve("secret"));
        Files.writeString(root.resolve("secret/private.md"), "top secret");

        ContentProperties props = new ContentProperties(
                root.toString(),
                List.of(new ContentProperties.Category(
                        "lld", "LLD", "Low-level design", List.of("lld/parking-lot")))
        );
        service = new ContentService(props);
    }

    @Test
    void categoriesReflectConfiguration() {
        List<CategoryDto> cats = service.categories();
        assertThat(cats).extracting(CategoryDto::id).containsExactly("lld");
        assertThat(cats).extracting(CategoryDto::label).containsExactly("LLD");
    }

    @Test
    void treeListsAllowedFilesAndExcludesBuildDirsAndBinaries() {
        TreeNode root = service.tree("lld");
        // The synthetic root's single child is the parking-lot directory.
        TreeNode parkingLot = root.children().get(0);
        assertThat(parkingLot.name()).isEqualTo("parking-lot");

        List<String> names = flattenFileNames(parkingLot);
        assertThat(names).contains("README.md", "Main.java");
        assertThat(names).doesNotContain("ignored.class"); // target/ excluded
        assertThat(names).doesNotContain("notes.bin");      // extension not allowed
    }

    @Test
    void readsMarkdownFileWithMarkdownFlag() {
        FileContent fc = service.file("lld", "lld/parking-lot/README.md");
        assertThat(fc.markdown()).isTrue();
        assertThat(fc.ext()).isEqualTo("md");
        assertThat(fc.content()).contains("# Parking Lot");
    }

    @Test
    void readsCodeFileWithoutMarkdownFlag() {
        FileContent fc = service.file("lld", "lld/parking-lot/src/Main.java");
        assertThat(fc.markdown()).isFalse();
        assertThat(fc.ext()).isEqualTo("java");
        assertThat(fc.content()).contains("class Main");
    }

    @Test
    void unknownCategoryIsRejected() {
        assertThatThrownBy(() -> service.tree("nope"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Unknown category");
    }

    @Test
    void pathTraversalOutsideCategoryIsBlocked() {
        // Try to escape the category path to read secret/private.md.
        assertThatThrownBy(() -> service.file("lld", "../secret/private.md"))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> service.file("lld", "secret/private.md"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void disallowedExtensionIsBlocked() {
        assertThatThrownBy(() -> service.file("lld", "lld/parking-lot/notes.bin"))
                .isInstanceOf(ResponseStatusException.class);
    }

    private List<String> flattenFileNames(TreeNode node) {
        if (node.type().equals("file")) {
            return List.of(node.name());
        }
        return node.children().stream()
                .flatMap(c -> flattenFileNames(c).stream())
                .toList();
    }
}
