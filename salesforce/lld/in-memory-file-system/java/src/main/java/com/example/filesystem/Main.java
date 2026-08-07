package com.example.filesystem;

import com.example.filesystem.service.InMemoryFileSystem;

/**
 * Runnable demo for the MVP operations. The output is deterministic and intentionally mirrors the
 * Python demo line-for-line so reviewers can compare both implementations quickly.
 */
public class Main {

    public static void main(String[] args) {
        InMemoryFileSystem fs = new InMemoryFileSystem();

        fs.mkdir("/docs/projects");
        fs.addContentToFile("/docs/projects/notes.txt", "Hello");
        fs.addContentToFile("/docs/projects/notes.txt", ", FileSystem!");

        System.out.println("ls / -> " + fs.ls("/"));
        System.out.println("ls /docs/projects -> " + fs.ls("/docs/projects"));
        System.out.println("read /docs/projects/notes.txt -> "
                + fs.readContentFromFile("/docs/projects/notes.txt"));
        System.out.println("ls /docs/projects/notes.txt -> " + fs.ls("/docs/projects/notes.txt"));
    }
}
