# Vending Machine — LLD Machine Coding

Full MVP of a thread-safe vending machine, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.vending.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m vending.main` |

Both suites cover 6 core cases, including a 50-thread race for the last unit of inventory.

## What it demonstrates
- **State pattern**: IDLE, HAS_MONEY, DISPENSING, SOLD_OUT each own valid operations/transitions.
- **Inventory management**: product catalog is immutable; stock decrements only during dispense.
- **Greedy change-making**: accepted denominations `[25, 10, 5, 1]`.
- **Concurrency**: synchronized methods / `RLock` serialize transaction-state mutations so stock never goes negative.

See each language README for UML diagrams, design decisions, code flow, run steps, and extension ideas.
