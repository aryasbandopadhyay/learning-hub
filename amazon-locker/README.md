# Amazon Locker — LLD Machine Coding

Full MVP of a thread-safe Amazon Locker location, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.locker.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m locker.main` |

Both suites are green (7 tests each, including a 50-threads-race-for-5-lockers concurrency test) and
both demos print identical output.

## What it demonstrates
- **OOP**: package and locker size modelling with clear fit rules
- **Strategy**: pluggable locker assignment (`SmallestFitAssignmentStrategy`)
- **Factory**: package/locker creation decoupled from constructors
- **State**: locker moves between `FREE` and `OCCUPIED`
- **Concurrency**: atomic per-locker claim (synchronized / Lock) plus one-time pickup-code consume

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope
rationale.
