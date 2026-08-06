# Chess — LLD Machine Coding

Focused MVP of Chess, implemented **twice** with an intentionally 1:1 design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.chess.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m chess.main` |

Both suites cover 10 deterministic tests and both demos print the same short opening.

## What it demonstrates
- **OOP**: abstract `Piece` hierarchy with `King/Queen/Rook/Bishop/Knight/Pawn`
- **Polymorphism**: `Game.makeMove` delegates movement to `piece.isValidMove(...)`
- **Inheritance**: shared color/common helpers live in `Piece`
- **Clean separation**: `Board` stores pieces/path checks; `Game` owns turns/captures
- **MVP scope discipline**: legal movement, capture, turns, and simple check detection; no full engine

See each language README for UML, sequence diagram, design decisions, code flow, and extension notes.
