# Snake & Ladder — LLD Machine Coding

Full MVP of a turn-based Snake & Ladder game, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.snakeladder.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m snakeladder.main` |

Both suites are deterministic (scripted dice, no random sleeps/flakiness) and both demos print
identical output.

## What it demonstrates
- **OOP**: `Board`, `Cell`, `Jump`, `Player`, and `Game` each own one clear responsibility
- **Strategy**: pluggable `Dice` abstraction; production uses random dice, tests use scripted dice
- **Validation**: snakes, ladders, board size, and duplicate jump starts are checked at construction
- **Turn flow**: roll → move → apply snake/ladder → exact-cell win; overshoot leaves the player put

Thread-safety is intentionally not part of this MVP: one game is turn-based and driven by one caller.
See each language's README for UML diagrams, design-decision tables, code flow, and extension notes.
