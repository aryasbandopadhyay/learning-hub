# Parking Lot — LLD Machine Coding

Full MVP of a thread-safe, multi-level parking lot, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.parkinglot.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m parking_lot.main` |

Both suites are green (7 tests each, including a 50-threads-race-for-5-spots concurrency test) and
both demos print identical output.

## What it demonstrates
- **OOP**: abstract `Vehicle` hierarchy, polymorphic `requiredSize()`
- **Strategy**: pluggable fee calculation + spot assignment
- **Factory**: vehicle creation decoupled from concrete classes
- **Concurrency**: atomic per-spot claim (synchronized / Lock) → no double-allocation, with an
  injected clock for deterministic fee tests

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope
rationale.
