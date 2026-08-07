# Connection Pool — LLD Machine Coding

Full MVP of a bounded, thread-safe Generic Object / Connection Pool, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.pool.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m pool.main` |

Both suites are green and both demos print identical output.

## What it demonstrates
- **OOP**: fake `Connection` resource with id/open/close/isValid lifecycle
- **Factory**: resource creation decoupled from the pool
- **Strategy**: pluggable validation-on-borrow
- **Concurrency**: bounded blocking pool with timeout, ownership checks, and no double hand-out
- **Correctness**: rejects timeout, foreign release, and double release

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope rationale.
