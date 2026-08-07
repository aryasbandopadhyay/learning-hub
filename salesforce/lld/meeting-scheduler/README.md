# Meeting Scheduler — LLD Machine Coding

Full MVP of a thread-safe meeting room scheduler/calendar, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.scheduler.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m scheduler.main` |

Both suites are green (5 tests each, including a 50-threads-race-for-5-rooms concurrency test) and
both demos print identical output.

## What it demonstrates
- **OOP**: rooms own calendars, immutable bookings, explicit half-open interval model
- **Strategy**: pluggable room selection (first-available vs least-used)
- **Concurrency**: atomic per-room conflict-check-and-insert → no double-booking
- **Algorithms**: Meeting Rooms II sweep-line utility for minimum room count

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope
rationale.
