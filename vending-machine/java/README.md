# Vending Machine — LLD Machine Coding (Java)

An end-to-end MVP of a vending machine, built for an SDE2 machine-coding round. It demonstrates
OOP modelling, the **State** pattern as the centerpiece, inventory management, greedy change-making,
and **thread-safe** purchase flow with no oversell.

> A parallel Python implementation lives in `../python` with its own README. Both produce identical
> demo output.

---

## 1. Why this MVP?

A machine-coding interviewer looks for clean OOP, one meaningful pattern, correct concurrency, and
working tests in limited time. The MVP is the **smallest vending system that still proves those**:

**In scope**
- Products with code/name/price and mutable stock.
- Accepted denominations: 1, 5, 10, 25.
- `insertMoney` → `selectProduct` → `dispense` → greedy change → back to IDLE.
- `cancel` returns the full inserted amount.
- **State pattern**: IDLE, HAS_MONEY, DISPENSING, SOLD_OUT.
- One physical machine serves one transaction at a time using synchronized methods.

**Deliberately out of scope** (extension points): card payments, exact-change-only edge cases beyond
greedy change, telemetry, persistence, REST/UI, and multi-select carts.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class VendingMachine {
      -Map inventory
      -State state
      -int balance
      -Product selectedProduct
      +insertMoney(amount) MoneyResult
      +selectProduct(code) PurchaseResult
      +dispense() PurchaseResult
      +cancel() RefundResult
      +purchase(code, coins) PurchaseResult
    }
    class State {
      <<interface>>
      +insertMoney(machine, amount)
      +selectProduct(machine, code)
      +dispense(machine)
      +cancel(machine)
    }
    class IdleState
    class HasMoneyState
    class DispensingState
    class SoldOutState
    class Product
    class InventoryItem
    class PurchaseResult
    class RefundResult

    State <|.. IdleState
    State <|.. HasMoneyState
    State <|.. DispensingState
    State <|.. SoldOutState
    VendingMachine --> State
    VendingMachine o-- InventoryItem
    InventoryItem --> Product
    PurchaseResult --> Product
```

### State transition diagram
```mermaid
stateDiagram-v2
    [*] --> IDLE
    IDLE --> HAS_MONEY: insertMoney
    HAS_MONEY --> HAS_MONEY: insertMoney / insufficient funds
    HAS_MONEY --> SOLD_OUT: select out-of-stock
    SOLD_OUT --> HAS_MONEY: preserve money for another choice
    HAS_MONEY --> DISPENSING: select in-stock with enough money
    DISPENSING --> IDLE: decrement stock + return change
    HAS_MONEY --> IDLE: cancel / refund
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **State interface + concrete states** | Removes large conditionals; each state rejects invalid operations clearly. |
| **Transient SOLD_OUT state** | Models the rejection branch without losing inserted money; customer can choose again or cancel. |
| **Product immutable, stock mutable** | Catalog data is stable; only inventory count changes. |
| **Greedy change from accepted denominations** | Keeps MVP focused and easy to reason about. |
| **`synchronized` public methods** | One machine services one transaction at a time; state/balance/stock mutate atomically. |
| **Atomic `purchase` helper** | Concurrency tests model a complete customer transaction under one monitor. |

### Concurrency model (the key part)
`VendingMachine` is the aggregate root and lock boundary. Every public operation is
`synchronized`, and `purchase(code, coins)` holds the monitor for a whole customer transaction.
When 50 threads race to buy the final `WATER`, exactly one reaches `completeDispense`, stock is
decremented once, and the final stock is `0` — asserted in `concurrentBuyersCannotOversellLastUnit`.

---

## 4. Code flow

```
Main → VendingMachine.insertMoney → current State.insertMoney
     → VendingMachine.selectProduct → HasMoneyState validates stock/funds
     → DispensingState.dispense → decrement stock → greedy change → IDLE
cancel → HasMoneyState.cancel → greedy refund → IDLE
```

Package layout:
```
com.example.vending
├── model/      Product, InventoryItem, MachineStateName, result records
├── state/      State interface + IDLE/HAS_MONEY/DISPENSING/SOLD_OUT implementations
├── service/    VendingMachine aggregate root and transaction lock
├── exception/  domain-specific failure types
└── Main.java   runnable demo
```

---

## 5. How to run

Prerequisites: JDK 17+ and Maven.

```powershell
cd java

# run the test suite (6 tests incl. the concurrency race test)
mvn test

# run the demo
mvn -q compile exec:java "-Dexec.mainClass=com.example.vending.Main"
```

Expected demo output:
```
Stock at open: WATER=2, CHIPS=3, SODA=1
Dispensed WATER, change = []
WATER stock now: 1
Dispensed CHIPS, change = [10]
Transaction cancelled, refund = [10, 5]
State now: IDLE
```

---

## 6. Tests

`VendingMachineTest` covers:
- exact-change purchase → product dispensed, stock decremented, IDLE
- overpayment → greedy change
- insufficient funds → rejected, stays HAS_MONEY
- out-of-stock selection → rejected, money preserved
- cancel → full refund and IDLE
- **concurrency**: 50 buyers race for 1 item → exactly 1 success, stock never negative

---

## 7. Extending
- **Card payments**: add a PaymentStrategy before DISPENSING.
- **Exact-change-only mode**: track coin inventory counts, not just denominations.
- **Telemetry**: publish events on state transitions and dispenses.
- **Multi-select**: replace selected product with a cart and checkout state.
