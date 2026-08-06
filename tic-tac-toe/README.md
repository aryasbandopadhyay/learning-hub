# Tic Tac Toe — LLD Machine Coding

Full MVP of a turn-based Tic Tac Toe engine, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.tictactoe.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m tictactoe.main` |

Both suites are green (5 tests each) and both demos print identical output.

## What it demonstrates
- **OOP**: `Board`, `Cell`, `Player`, and `Game` each have one responsibility
- **Enums**: `Mark` and `GameStatus` make state explicit and testable
- **Validation**: bounds, occupied cells, wrong turns, and post-game moves are rejected clearly
- **Determinism**: no concurrency or timing concerns; Tic Tac Toe is single-threaded/turn-based

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope
rationale.
