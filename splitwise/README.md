# Splitwise — LLD Machine Coding

Full MVP of a thread-safe Splitwise-style expense splitter, implemented **twice** with an identical
design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java; mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.splitwise.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python; python -m pytest -q` | `python -m splitwise.main` |

Both suites cover EQUAL, EXACT, PERCENT, pairwise netting, and concurrent `addExpense` / `add_expense`.

## What it demonstrates
- **OOP**: `User`, immutable `Expense`, computed `Split`, balance snapshot
- **Strategy**: `EqualSplitStrategy`, `ExactSplitStrategy`, `PercentSplitStrategy`
- **Money precision**: integer cents, never floating-point arithmetic for balances
- **Concurrency**: synchronized / `RLock` guarded writes so shared balances do not lose updates
