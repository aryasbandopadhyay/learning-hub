# Elevator System — LLD Machine Coding

Full MVP of a deterministic elevator system, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.elevator.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m elevator.main` |

Both suites are green (4 tests each, including 50-thread request-intake tests) and both demos print
identical output.

## What it demonstrates
- **OOP**: `Elevator`, request models, controller facade
- **State**: `IDLE`, `MOVING_UP`, `MOVING_DOWN`, `DOORS_OPEN`
- **Strategy**: pluggable `SchedulingStrategy`, implemented by nearest-car scheduling
- **Concurrency**: thread-safe request intake (BlockingQueue / Lock + deque)
- **Determinism**: no sleeps or movement threads; tests advance simulation with `step()`

See each language README for UML, design decisions, code flow, scope rationale, and extensions.
