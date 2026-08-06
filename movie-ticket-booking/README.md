# Movie Ticket Booking — LLD Machine Coding

Full MVP of a booking/payment-focused movie ticket system, implemented **twice** with an identical
design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.movieticket.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m movieticket.main` |

Both versions model the same state machine: `CREATED -> SEATS_HELD -> PAYMENT_PENDING -> CONFIRMED`,
with `EXPIRED` and `FAILED` terminal paths that release seats. The MVP is deliberately narrower than
a discovery-oriented BookMyShow design: it focuses on seat holds, expiry, payment, and locking.

## What it demonstrates
- **State machine**: guarded booking lifecycle with illegal transition rejection
- **Strategy/abstraction**: injectable `PaymentProcessor`
- **Concurrency**: show-level lock makes multi-seat holds all-or-nothing and prevents double-booking
- **Testability**: injected mutable clock for hold expiry/payment timeout tests

See each language README for UML diagrams, sequence/state diagrams, design-decision tables, code
flow, run steps, expected demo output, test coverage, and extensions.
