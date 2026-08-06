# Library Management — LLD Machine Coding

Full MVP of a thread-safe library catalog/lending system, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java; mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.library.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python; python -m pytest -q` | `python -m library.main` |

Both suites are green (5 tests each, including a 50-threads-race-for-1-copy concurrency test) and
both demos print similar output.

## What it demonstrates
- **OOP/entity modelling**: `Book` title metadata vs physical `BookItem` copies
- **Relationships**: `Book` 1..* `BookItem`; `Member` 1..* `Loan`
- **Strategy**: pluggable overdue fine calculation
- **Concurrency**: atomic per-copy checkout (synchronized / Lock) → no double-loaning, with an
  injected clock for deterministic due-date and fine tests

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope
rationale.
