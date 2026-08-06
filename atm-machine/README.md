# ATM Machine — LLD Machine Coding

Full MVP of a thread-safe ATM machine, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.atm.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m atm.main` |

Both suites cover 8 tests, including a 50-sessions-race-for-limited-balance concurrency test.

## What it demonstrates
- **State pattern**: `IDLE → CARD_INSERTED → AUTHENTICATED → DISPENSING → AUTHENTICATED`, plus eject paths
- **Money modelling**: integer cents/paise, never floating point for balances
- **Cash dispensing**: greedy exact denomination breakdown with ATM inventory decrement
- **Concurrency**: atomic account withdrawal (`synchronized` / `Lock`) so concurrent sessions cannot overdraw

See each language's README for UML diagrams, state transition diagram, design decisions, run steps,
test coverage, and extension ideas.
