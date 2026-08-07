package com.example.filesystem;

import com.example.filesystem.service.InMemoryFileSystem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSystemTest {

    @Test
    void mkdirCreatesNestedDirectoriesAndLsShowsChildren() {
        InMemoryFileSystem fs = new InMemoryFileSystem();
        fs.mkdir("/a/b/c");
        assertEquals(List.of("a"), fs.ls("/"));
        assertEquals(List.of("b"), fs.ls("/a"));
        assertEquals(List.of("c"), fs.ls("/a/b"));
    }

    @Test
    void addContentCreatesAppendsAndReadsFile() {
        InMemoryFileSystem fs = new InMemoryFileSystem();
        fs.addContentToFile("/a/b/file.txt", "Hello");
        fs.addContentToFile("/a/b/file.txt", " World");
        assertEquals("Hello World", fs.readContentFromFile("/a/b/file.txt"));
    }

    @Test
    void lsOnRootIsLexicographic() {
        InMemoryFileSystem fs = new InMemoryFileSystem();
        fs.mkdir("/zeta");
        fs.mkdir("/alpha");
        fs.addContentToFile("/middle.txt", "m");
        assertEquals(List.of("alpha", "middle.txt", "zeta"), fs.ls("/"));
    }

    @Test
    void lsOnFileReturnsOnlyFileName() {
        InMemoryFileSystem fs = new InMemoryFileSystem();
        fs.addContentToFile("/logs/today.txt", "entry");
        assertEquals(List.of("today.txt"), fs.ls("/logs/today.txt"));
    }

    @Test
    void nestedPathsSupportDirectoriesAndFilesTogether() {
        InMemoryFileSystem fs = new InMemoryFileSystem();
        fs.mkdir("/company/salesforce/docs");
        fs.addContentToFile("/company/salesforce/docs/design.md", "LLD");
        fs.addContentToFile("/company/salesforce/readme.txt", "root doc");
        assertEquals(List.of("docs", "readme.txt"), fs.ls("/company/salesforce"));
        assertEquals("LLD", fs.readContentFromFile("/company/salesforce/docs/design.md"));
    }

    /**
     * Concurrency test: many threads create different files under the same parent tree. The
     * write-lock should serialize mutations enough that no child entry is lost or corrupted.
     */
    @Test
    void concurrentWritersToDifferentFilesDoNotCorruptTree() throws InterruptedException {
        InMemoryFileSystem fs = new InMemoryFileSystem();
        int writers = 40;
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger completed = new AtomicInteger();

        for (int i = 0; i < writers; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await(); // release all writers together for maximum tree contention
                    fs.addContentToFile("/shared/file-" + id + ".txt", "content-" + id);
                    completed.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(writers, completed.get());
        assertEquals(writers, fs.ls("/shared").size());
        for (int i = 0; i < writers; i++) {
            assertEquals("content-" + i, fs.readContentFromFile("/shared/file-" + i + ".txt"));
        }
    }
}
