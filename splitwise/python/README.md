# Splitwise — LLD Machine Coding (Python)

An end-to-end MVP of Splitwise, built for an SDE2 machine-coding round. It demonstrates OOP
modelling, the **Strategy** pattern, exact money arithmetic, and **thread-safe** concurrent expense
recording.

> A parallel Java implementation lives in `../java` with its own README. Both produce identical
> demo output.

---

## 1. Why this MVP?

A machine-coding interviewer looks for clean object modelling, one central pattern applied for a real
reason, correctness under edge cases, and working tests. This MVP is the **smallest system that still
exercises all of those**:

**In scope**
- Users, expenses, computed splits, and a net balance sheet
- `add_expense` → run a `SplitStrategy` → net debtor/creditor balances
- EQUAL, EXACT, and PERCENT split algorithms
- `get_balances(user)` plus `show_balances()` overview
- Thread-safe concurrent `add_expense`

**Deliberately out of scope** (extension points): groups UI, debt simplification/minimization,
multi-currency, persistence, settlements history. Debt simplification is useful but not needed for
this MVP because the balance sheet already nets every pair.

**Money choice:** all amounts are `int` cents. We never use `float` for business arithmetic, so
rounding bugs cannot corrupt balances.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class User {
      +str id
      +str name
    }
    class Expense {
      +str id
      +User payer
      +int total_cents
      +tuple splits
    }
    class Split {
      +User user
      +int amount_cents
    }
    class BalanceSummary {
      +Mapping owes
      +Mapping owed_by
    }
    class ExpenseManager {
      -list expenses
      -dict balances
      -RLock lock
      +add_expense(...) Expense
      +get_balances(User) BalanceSummary
      +show_balances() str
    }
    class SplitStrategy {
      <<abstract>>
      +split(total, participants, values) list
    }
    class EqualSplitStrategy
    class ExactSplitStrategy
    class PercentSplitStrategy

    SplitStrategy <|-- EqualSplitStrategy
    SplitStrategy <|-- ExactSplitStrategy
    SplitStrategy <|-- PercentSplitStrategy
    ExpenseManager --> SplitStrategy
    ExpenseManager o-- Expense
    Expense o-- Split
    Split --> User
    Expense --> User
    BalanceSummary --> User
```

### add_expense sequence
```mermaid
sequenceDiagram
    participant C as Client
    participant M as ExpenseManager
    participant S as SplitStrategy
    participant B as Balance Sheet
    C->>M: add_expense(payer,total,participants,strategy,values)
    M->>S: split(total, participants, values)
    S-->>M: list[Split]
    M->>M: Expense(payer,total,splits)
    loop each non-payer split
        M->>B: _add_debt(participant, payer, share) «RLock»
        B-->>M: net against opposite debt
    end
    M-->>C: Expense
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **Strategy for splitting** | Split algorithms change independently from balance-sheet mutation. |
| **Frozen dataclasses** | `Expense` and `Split` are safe snapshots after validation. |
| **Integer cents (`int`)** | Exact money math; no floating-point rounding drift. |
| **Normalized directed balances** | Store only one net direction for each pair: `debtor -> creditor`. |
| **Pairwise netting on write** | `A owes B` then `B owes A` collapses immediately, making reads simple. |
| **`RLock` around add_expense** | One mutation touches expenses and balances; locking prevents lost updates. |

---

## 4. Code flow

```
main → ExpenseManager.add_expense
        → SplitStrategy.split (Equal / Exact / Percent)
        → Expense
        → for each non-payer Split: _add_debt(debtor, payer, cents)
        → get_balances / show_balances read normalized balances
```

Module layout:
```
splitwise/
├── models.py       User, Split, Expense, BalanceSummary
├── strategies.py   SplitStrategy + Equal/Exact/Percent implementations
├── service.py      ExpenseManager
├── exceptions.py   InvalidSplitError
└── main.py         runnable demo
tests/
└── test_splitwise.py
```

---

## 5. How to run

Prerequisites: Python 3.10+.

```powershell
cd python

# run the test suite (5 tests incl. concurrent add_expense)
python -m pytest -q

# run the demo
python -m splitwise.main
```

Expected demo output:
```
Recorded expenses: 3
Balances:
Bob owes Alice $50.00
Charlie owes Alice $75.00
```

---

## 6. Tests

`tests/test_splitwise.py` covers:
- EQUAL split: A pays 300 for A/B/C → B and C each owe A 100
- EXACT split validation: bad sum rejected; valid exact shares update balances
- PERCENT split validation: bad percent sum rejected; valid percentages update balances
- pairwise netting across multiple expenses
- **concurrency**: 80 threads add equal expenses → final balance equals sequential result

---

## 7. Extending (what a follow-up would add)
- **Debt simplification/minimization**: reduce many pairwise debts into fewer settlement edges.
- **Groups**: reusable participant sets and group-level views.
- **Multi-currency**: currency per expense plus FX-rate strategy.
- **Settlements history**: record payments separately from expense creation.
- **Persistence/API**: repository abstraction and REST/UI layer.
