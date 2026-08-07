# Salesforce — Most-Asked LLD / Machine-Coding Problems (Index)

The machine-coding half of the Salesforce loop. Each problem below is either **already implemented**
elsewhere in this repo (linked to the **LLD tab** — no duplication) or **built fresh** here as a
complete, dual-language (**Java + Python**) project with unit tests and a UML README, matching the
exact format of the existing LLD solutions.

## New builds (this section) — full code + tests + docs
Each folder has `java/` (Maven + JUnit 5), `python/` (pytest), a top `README.md` with Mermaid UML,
a design-decision table, code flow, and run instructions.

| # | Problem | Why Salesforce asks it | Patterns / concepts | Folder |
| --- | --- | --- | --- | --- |
| 1 | **In-Memory Pub/Sub** | Platform Events / event bus flavour; concurrency. | Observer, thread-safe queues, at-least-once delivery | [`pub-sub-system/`](pub-sub-system/README.md) |
| 2 | **Meeting Room Scheduler** | Calendar/booking; interval conflict detection. | Strategy (room selection), sweep-line, concurrency | [`meeting-scheduler/`](meeting-scheduler/README.md) |
| 3 | **In-Memory File System** | LC 588; tree modelling & path parsing. | Composite, recursive traversal | [`in-memory-file-system/`](in-memory-file-system/README.md) |
| 4 | **Design HashMap** | LC 706; build a map from scratch. | Separate chaining, load-factor resize | [`custom-hashmap/`](custom-hashmap/README.md) |
| 5 | **Autocomplete / Typeahead** | LC 642; Trie + ranked top-k. | Trie, heap/top-k ranking | [`autocomplete-system/`](autocomplete-system/README.md) |
| 6 | **Connection / Object Pool** | Resource pooling & blocking borrow. | Factory, blocking queue / semaphore, timeout | [`connection-pool/`](connection-pool/README.md) |

## Already in the repo — see the **LLD tab** (referenced, not duplicated)
These commonly-asked Salesforce LLD problems already have full dual-language implementations in the
main **LLD** section:

| Problem | Salesforce relevance | LLD-tab folder |
| --- | --- | --- |
| **LRU / LFU Cache** | The single most-asked Salesforce design question. | `cache/` |
| **Parking Lot** | Classic OOP + concurrency warm-up. | `parking-lot/` |
| **Splitwise** | Ledger / settle-up logic. | `splitwise/` |
| **Elevator System** | Request scheduling & prioritisation. | `elevator-system/` |
| **Tic-Tac-Toe** | OOP modelling + win detection (LC 348). | `tic-tac-toe/` |
| **Snake & Ladder** | Board-game OOP. | `snake-and-ladder/` |
| **Rate Limiter** | Throttling / token bucket. | `rate-limiter/` |
| **Job Scheduler** | Queuing & concurrency. | `job-scheduler/` |
| **Library Management** | Borrow/return + fines. | `library-management/` |
| **Movie Ticket Booking / BookMyShow** | Seat locking + transactions. | `movie-ticket-booking/`, `bookmyshow/` |
| **Notification Service** | Event delivery / CRM notifications. | `notification-service/` |
| **Vending Machine / ATM** | State-machine OOP. | `vending-machine/`, `atm-machine/` |

> Open the **LLD** tab to browse any of the above with their own UML READMEs and tests.

## How each new build is scoped
The same discipline as the rest of the repo: the **smallest MVP that still exercises** clean OOP, at
least one design pattern applied for a real reason, correct concurrency where relevant, and a green
test suite — the exact bar a 45–60 minute machine-coding round is graded on. Every README ends with
an *Extending* section listing what a follow-up round would add.
