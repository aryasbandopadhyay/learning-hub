# BookMyShow — LLD Machine Coding

Full MVP of movie discovery plus concurrent seat booking, implemented **twice** with an identical
design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.bookmyshow.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m bookmyshow.main` |

Both suites target 7 tests each, including a 50-thread race for the same seat. Both demos print the
same flow: search → hold seats → confirm booking.

## What it demonstrates
- **OOP**: City → Theater → Screen → Show → Seat hierarchy
- **Clean layering**: model objects plus one application service
- **Concurrency**: per-show lock guards the seat map, making multi-seat holds all-or-nothing
- **Expiry**: injected clock + hold duration; expired holds release seats back to AVAILABLE

See each language's README for UML diagrams, design-decision tables, code flow, and MVP-scope
rationale.
