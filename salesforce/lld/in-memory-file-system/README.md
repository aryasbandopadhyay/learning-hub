# In-Memory File System — LLD Machine Coding

Full MVP of a thread-safe in-memory file system (LeetCode 588 flavor), implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `java -cp target/classes com.example.filesystem.Main` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m filesystem.main` |

Both suites are green and both demos print identical output.

## What it demonstrates
- **OOP + Composite**: `FileSystemEntry` base type, `Directory` containing child entries, `FileEntry` storing content
- **Tree traversal**: normalized absolute paths, recursive `mkdir`, create-or-append files
- **Concurrency**: read/write lock around tree operations so readers can share the tree and writers mutate safely
- **Determinism**: lexicographic directory listing via sorted child maps

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope rationale.
