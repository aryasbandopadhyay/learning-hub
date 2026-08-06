# Job Scheduler — LLD Machine Coding

Full MVP of a deterministic, thread-safe job scheduler, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.scheduler.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m scheduler.main` |

Both suites cover one-shot scheduling, ordering, recurring jobs, cancellation, and concurrent submissions.

## What it demonstrates
- **Priority-queue scheduling**: min-heap ordered by next run time.
- **Command pattern**: `Job` wraps an id and executable action.
- **Deterministic time**: injected clock + explicit `tick(now)` instead of flaky sleeps.
- **Concurrency**: a lock guards the heap, so many producer threads can schedule safely.

See each language's README for UML diagrams, design-decision tables, code flow, and MVP-scope rationale.
