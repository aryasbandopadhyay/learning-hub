# Car Rental — LLD Machine Coding

Full MVP of a thread-safe car rental system, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java; mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.carrental.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python; python -m pytest -q` | `python -m carrental.main` |

Both versions demonstrate:
- **Booking workflow**: `CONFIRMED -> PICKED_UP -> RETURNED`, or `CONFIRMED -> CANCELLED`
- **Strategy**: `PricingStrategy` for daily-rate pricing today, weekend/seasonal/loyalty later
- **Concurrency**: per-car lock around date-overlap check + reservation insert, so overlapping
  double-booking cannot happen even under a 50-thread race

Out of scope by design: customers/accounts, payments, insurance add-ons, one-way rentals, damage or
late fees, refunds.

See each language README for UML diagrams, design decisions, code flow, run steps, expected demo
output, tests, and extension ideas.
