# Find Command — LLD Machine Coding (Java)

An end-to-end MVP of a Linux `find` command, built for an SDE2 machine-coding round. It models an
in-memory filesystem and demonstrates the **Composite** and **Specification / Filter** patterns with
composable boolean filters.

> A parallel Python implementation lives in `../python` with its own README. Both use the same
> package layout and produce equivalent demo output.

---

## 1. Why this MVP?

A machine-coding interviewer looks for clean OOP, design patterns applied for a real reason,
readable code, and working tests — in ~45 minutes. The MVP is the **smallest system that still
exercises all of those**:

**In scope**
- In-memory tree, not the real disk, so tests are deterministic and portable
- `FileSystemEntry` → `FileNode` / `DirectoryNode` Composite
- `Filter` abstraction with name, extension, size, type, and min-depth filters
- `AndFilter`, `OrFilter`, and `NotFilter` to compose criteria like real `find`
- `FindEngine.find(root, filter)` DFS traversal returning matched paths
- Return both files and directories depending on the filter

**Deliberately out of scope** (extension points, not core learning value): real filesystem I/O,
`-exec` actions, permissions/mtime filters, symlink handling, and regex names. The design cleanly
allows a real-FS adapter later.

Thread-safety is not needed here: the search is read-only over an effectively immutable in-memory
tree. The engine is also trivially parallelizable because matching each subtree has no shared state.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class FileSystemEntry {
      <<abstract>>
      +String name
      +long sizeBytes
      +EntryType type
    }
    class FileNode {
      +String extension
    }
    class DirectoryNode {
      +List~FileSystemEntry~ children
      +addChild(entry) DirectoryNode
    }
    FileSystemEntry <|-- FileNode
    FileSystemEntry <|-- DirectoryNode
    DirectoryNode o-- FileSystemEntry

    class Filter {
      <<interface>>
      +matches(entry, depth) bool
    }
    class NameFilter
    class ExtensionFilter
    class SizeFilter
    class TypeFilter
    class MinDepthFilter
    class AndFilter
    class OrFilter
    class NotFilter
    Filter <|.. NameFilter
    Filter <|.. ExtensionFilter
    Filter <|.. SizeFilter
    Filter <|.. TypeFilter
    Filter <|.. MinDepthFilter
    Filter <|.. AndFilter
    Filter <|.. OrFilter
    Filter <|.. NotFilter
    AndFilter o-- Filter
    OrFilter o-- Filter
    NotFilter --> Filter

    class FindEngine {
      +find(root, filter) List~String~
      +findEntries(root, filter) List~FileSystemEntry~
    }
    FindEngine --> Filter
    FindEngine --> DirectoryNode
```

### Traversal / matching sequence
```mermaid
sequenceDiagram
    participant C as Client
    participant E as FindEngine
    participant D as DirectoryNode
    participant F as Filter
    C->>E: find(root, filter)
    E->>D: visit root at depth 0
    E->>F: matches(entry, depth)
    F-->>E: true / false
    loop each directory child
        E->>E: dfs(child, path, depth + 1)
        E->>F: matches(child, depth)
    end
    E-->>C: matched paths
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **Composite for the tree** | `FindEngine` treats files and directories uniformly while only directories own children. |
| **Specification / Filter interface** | One criterion = one class, so each rule is testable and open for extension. |
| **Combinators over hardcoded queries** | Real `find` expressions compose; `And/Or/Not` avoids a giant `if` ladder in the engine. |
| **In-memory model** | Keeps tests deterministic, portable, and fast; a real-FS adapter can build the same tree. |
| **DFS in `FindEngine`** | Simple, predictable output order; easy to swap for BFS or parallel traversal later. |
| **Depth passed to filters** | Supports `MinDepthFilter` without making every entry store traversal context. |
| **No locking** | Read-only search over an immutable tree has no shared mutable state to protect. |

---

## 4. Code flow

```
Main → build DirectoryNode/FileNode tree → compose Filter objects
FindEngine.find → dfs(root)
        → Filter.matches(entry, depth)
        → add path if matched
        → recurse into DirectoryNode children
```

Package layout:
```
com.example.find
├── model/      FileSystemEntry, FileNode, DirectoryNode, EntryType
├── filter/     Filter + Name/Extension/Size/Type/MinDepth + And/Or/Not
├── engine/     FindEngine traversal service
├── exception/  InvalidFileSystemException
└── Main.java   runnable demo
```

---

## 5. How to run

Prerequisites: JDK 17+ and Maven.

```powershell
cd java

# run the test suite (7 tests)
mvn test

# run the demo
mvn -q compile exec:java "-Dexec.mainClass=com.example.find.Main"
```

Expected demo output:
```
Text files:
  /workspace/docs/readme.txt
  /workspace/notes.txt
Large log files:
  /workspace/src/logs/app.log
Directories:
  /workspace
  /workspace/docs
  /workspace/src
  /workspace/src/logs
```

---

## 6. Tests

`FindEngineTest` covers:
- `NameFilter` glob `*.txt`
- `ExtensionFilter` and `SizeFilter.greaterThan`
- `TypeFilter(DIRECTORY)` returning directories, including root
- `AndFilter` for name glob + size
- nested `OrFilter` + `NotFilter` composition
- DFS reaching nested paths deeper than one level with `MinDepthFilter`

---

## 7. Extending (what a follow-up would add)
- **Real filesystem adapter**: read disk metadata and build `DirectoryNode` / `FileNode` objects.
- **`-exec` actions**: introduce a Visitor or Action abstraction executed on each match.
- **Regex names**: add `RegexNameFilter` without changing the engine.
- **mtime / permissions**: add more Specification classes.
- **Symlink handling**: add node metadata and cycle detection policy.
- **Parallel traversal**: split DFS by directory because filters are stateless.
