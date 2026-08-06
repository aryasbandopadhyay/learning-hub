# Snake & Ladder — LLD Machine Coding (Java)

An end-to-end MVP of Snake & Ladder, built for an SDE2 machine-coding round. It demonstrates clean
OOP modelling, a Strategy-style **Dice** abstraction, deterministic tests, and a focused turn-based
game loop.

> A parallel Python implementation lives in `../python` with its own README. Both produce identical
> demo output. The class structure is intentionally 1:1 between the two languages.

---

## 1. Why this MVP?

A machine-coding interviewer looks for: clear entities, simple APIs, validation, deterministic tests,
and well-explained trade-offs. The MVP is the **smallest playable system that still exercises all of
those**:

**In scope**
- Configurable board (default 100 cells)
- Snakes (`head -> tail`, downward) and ladders (`bottom -> top`, upward)
- Validation that no two jumps start on the same cell
- Multiple players in fixed turn order
- `playTurn()` → roll → move → apply jump → exact last-cell win detection
- Overshoot rule: if a roll exceeds the last cell, the player stays in place
- Pluggable **Dice** strategy: random for production, scripted/seeded for deterministic tests

**Deliberately out of scope** (extension points, not core learning value): variable dice rules,
multiple dice, configurable exact-roll-to-win toggle, GUI/network play, persistence/DB.

Thread-safety is not required here: a single game is turn-based and driven by one caller. Adding
locking would make the teaching surface noisier without improving this MVP.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class Board {
      +int size
      +applyJump(cell) int
      +findJump(cell) Optional~Jump~
    }
    class Cell {
      +int number
      +Optional~Jump~ jump
    }
    class Jump {
      +int from
      +int to
      +JumpType type
      +snake(head, tail) Jump
      +ladder(bottom, top) Jump
    }
    class JumpType {
      <<enum>>
      SNAKE
      LADDER
    }
    class Player {
      +String name
      +int position
      +moveTo(position)
    }
    class Dice {
      <<interface>>
      +roll() int
    }
    class RandomDice
    class ScriptedDice
    class Game {
      +playTurn() MoveResult
      +playToCompletion() Player
      +getWinner() Optional~Player~
    }
    class MoveResult

    Dice <|.. RandomDice
    Dice <|.. ScriptedDice
    Board o-- Cell
    Cell --> Jump
    Jump --> JumpType
    Game --> Board
    Game --> Dice
    Game o-- Player
    Game --> MoveResult
```

### Turn sequence
```mermaid
sequenceDiagram
    participant C as Client
    participant G as Game
    participant D as Dice
    participant B as Board
    participant P as Player
    C->>G: playTurn()
    G->>D: roll()
    D-->>G: value
    G->>G: attempted = position + roll
    alt attempted > last cell
        G->>G: stay in place (overshoot rule)
    else on board
        G->>B: findJump(attempted)
        B-->>G: snake / ladder / empty
        G->>B: applyJump(attempted)
        B-->>G: final cell
        G->>P: moveTo(final cell)
    end
    G->>G: check final cell == board.size
    G-->>C: MoveResult
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **`Board` owns validation** | Invalid snakes/ladders fail fast before a game starts, keeping `playTurn()` readable. |
| **`Cell` + `Jump` split** | A cell is a square; a jump is a directed edge. This mirrors the real board and teaches composition. |
| **`Jump.snake` / `Jump.ladder` factories** | Direction rules live beside construction, so callers cannot accidentally create an inverted snake/ladder. |
| **`Dice` interface** | Strategy pattern: random dice in real runs, `ScriptedDice` in tests/demos, seeded random if needed. |
| **Players start at cell 1** | Simple MVP convention; every roll moves from a real board cell. |
| **Exact-roll overshoot rule** | Common rule variant and easy to reason about: too-large rolls leave the token in place. |
| **No locking** | One game is turn-based and single-driver; thread-safety is unnecessary scope for this problem. |

---

## 4. Code flow

```
Main → Board(snakes/ladders) → Game(board, dice, players)
Game.playTurn → Dice.roll → compute attempted cell
              → Board.findJump/applyJump → Player.moveTo
              → check winner → MoveResult
Game.playToCompletion → repeat playTurn until winner exists
```

Package layout:
```
com.example.snakeladder
├── model/      Board, Cell, Jump, JumpType, Player
├── game/       Dice, RandomDice, ScriptedDice, Game, MoveResult
├── exception/  InvalidBoardException, GameAlreadyOverException
└── Main.java   runnable deterministic demo
```

---

## 5. How to run

Prerequisites: JDK 17+ and Maven.

```powershell
cd java

# run the test suite (5 deterministic tests)
mvn test

# run the demo
mvn -q compile exec:java "-Dexec.mainClass=com.example.snakeladder.Main"
```

Expected demo output:
```
Turn 1: Alice rolled 3 and moved 1 -> 4, ladder to 8
Turn 2: Bob rolled 4 and moved 1 -> 5
Turn 3: Alice rolled 2 and moved 8 -> 10 and won
Winner: Alice
```

---

## 6. Tests

`SnakeLadderTest` covers:
- ladder moves a player up when the scripted roll lands exactly on the ladder bottom
- snake moves a player down when the scripted roll lands exactly on the snake head
- full deterministic two-player game produces Alice as the winner
- overshoot rule: a too-large roll leaves the player in place
- invalid board: two jumps starting from the same cell are rejected

---

## 7. Extending (what a follow-up would add)
- **Variable dice rules**: swap or decorate `Dice` without editing `Game`.
- **Multiple dice**: introduce a `DiceSet` strategy returning the total and roll details.
- **Rule toggles**: inject a `WinRule` for exact-roll vs clamp-to-last-cell variants.
- **GUI/REST**: keep `Game` as the domain service and add controllers/adapters around it.
