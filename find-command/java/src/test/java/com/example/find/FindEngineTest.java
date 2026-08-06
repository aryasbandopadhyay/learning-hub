package com.example.find;

import com.example.find.engine.FindEngine;
import com.example.find.filter.AndFilter;
import com.example.find.filter.ExtensionFilter;
import com.example.find.filter.MinDepthFilter;
import com.example.find.filter.NameFilter;
import com.example.find.filter.NotFilter;
import com.example.find.filter.OrFilter;
import com.example.find.filter.SizeFilter;
import com.example.find.filter.TypeFilter;
import com.example.find.model.DirectoryNode;
import com.example.find.model.EntryType;
import com.example.find.model.FileNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindEngineTest {

    private final FindEngine engine = new FindEngine();

    private DirectoryNode tree() {
        DirectoryNode root = new DirectoryNode("workspace");
        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("readme.txt", 120));
        docs.addChild(new FileNode("guide.txt", 1_500));
        docs.addChild(new FileNode("design.md", 300));

        DirectoryNode src = new DirectoryNode("src");
        src.addChild(new FileNode("app.py", 900));
        DirectoryNode logs = new DirectoryNode("logs");
        logs.addChild(new FileNode("app.log", 2_048));
        logs.addChild(new FileNode("old.log", 500));
        src.addChild(logs);

        root.addChild(docs);
        root.addChild(src);
        root.addChild(new FileNode("notes.txt", 2_000));
        return root;
    }

    @Test
    void nameFilterWithGlobReturnsTxtFiles() {
        List<String> paths = engine.find(tree(), new NameFilter("*.txt"));
        assertEquals(List.of(
                "/workspace/docs/readme.txt",
                "/workspace/docs/guide.txt",
                "/workspace/notes.txt"), paths);
    }

    @Test
    void extensionFilterReturnsMatchingFiles() {
        List<String> paths = engine.find(tree(), new ExtensionFilter(".log"));
        assertEquals(List.of(
                "/workspace/src/logs/app.log",
                "/workspace/src/logs/old.log"), paths);
    }

    @Test
    void sizeFilterReturnsLargeEntries() {
        List<String> paths = engine.find(tree(), SizeFilter.greaterThan(1_000));
        assertEquals(List.of(
                "/workspace/docs/guide.txt",
                "/workspace/src/logs/app.log",
                "/workspace/notes.txt"), paths);
    }

    @Test
    void typeFilterDirectoryReturnsOnlyDirectories() {
        List<String> paths = engine.find(tree(), new TypeFilter(EntryType.DIRECTORY));
        assertEquals(List.of(
                "/workspace",
                "/workspace/docs",
                "/workspace/src",
                "/workspace/src/logs"), paths);
    }

    @Test
    void andFilterComposesNameAndSize() {
        List<String> paths = engine.find(tree(), new AndFilter(
                new NameFilter("*.txt"),
                SizeFilter.greaterThan(1_000)));
        assertEquals(List.of(
                "/workspace/docs/guide.txt",
                "/workspace/notes.txt"), paths);
    }

    @Test
    void orAndNotFiltersComposeAcrossTwoLevels() {
        List<String> paths = engine.find(tree(), new AndFilter(
                new OrFilter(new ExtensionFilter(".txt"), new ExtensionFilter(".md")),
                new NotFilter(new NameFilter("readme.txt"))));
        assertEquals(List.of(
                "/workspace/docs/guide.txt",
                "/workspace/docs/design.md",
                "/workspace/notes.txt"), paths);
    }

    @Test
    void traversalReachesNestedDirectoriesWithMinDepth() {
        List<String> paths = engine.find(tree(), new AndFilter(
                new MinDepthFilter(3),
                new NameFilter("*.log")));
        assertEquals(List.of(
                "/workspace/src/logs/app.log",
                "/workspace/src/logs/old.log"), paths);
    }
}
