package com.example.find;

import com.example.find.engine.FindEngine;
import com.example.find.filter.AndFilter;
import com.example.find.filter.ExtensionFilter;
import com.example.find.filter.NameFilter;
import com.example.find.filter.SizeFilter;
import com.example.find.filter.TypeFilter;
import com.example.find.model.DirectoryNode;
import com.example.find.model.EntryType;
import com.example.find.model.FileNode;

/**
 * Runnable demo showing the end-to-end flow: build an in-memory tree, compose filters, and print
 * matched paths. Run with {@code mvn -q compile exec} or from the packaged jar / IDE.
 */
public class Main {

    public static void main(String[] args) {
        DirectoryNode root = sampleTree();
        FindEngine engine = new FindEngine();

        System.out.println("Text files:");
        engine.find(root, new NameFilter("*.txt")).forEach(path -> System.out.println("  " + path));

        System.out.println("Large log files:");
        engine.find(root, new AndFilter(new ExtensionFilter(".log"), SizeFilter.greaterThan(1_000)))
                .forEach(path -> System.out.println("  " + path));

        System.out.println("Directories:");
        engine.find(root, new TypeFilter(EntryType.DIRECTORY))
                .forEach(path -> System.out.println("  " + path));
    }

    private static DirectoryNode sampleTree() {
        DirectoryNode root = new DirectoryNode("workspace");
        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("readme.txt", 120));
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
}
