# Airline Reservation — LLD Machine Coding

Full MVP of a thread-safe airline reservation system, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.airline.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m airline.main` |

Both suites include a 50-threads-race-for-one-seat concurrency test.

## What it demonstrates
- **Inventory management**: each `Flight` owns a `FlightInventory` of `Seat`s.
- **Clean layering**: model, service, strategy, exception, demo, tests.
- **Strategy**: pluggable cabin pricing.
- **Concurrency**: atomic per-seat claim (`synchronized` / `Lock`) prevents double-booking.

See each language README for UML diagrams, design-decision tables, code flow, run steps, coverage,
and extension ideas.
