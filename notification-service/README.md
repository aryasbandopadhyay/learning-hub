# Notification Service — LLD Machine Coding

Full MVP of a thread-safe Notification Service, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.notification.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m notification.main` |

Both suites are green (6 tests each, including retry and a 50-thread concurrency test) and both demos
print equivalent output.

## What it demonstrates
- **Strategy**: `NotificationChannel` with Email/SMS/Push implementations
- **Factory**: `ChannelFactory` maps `ChannelType` to a configured strategy
- **Observer**: listeners receive final SENT/FAILED events, demonstrated by `AuditListener`
- **Concurrency**: thread-safe channel sinks/listener event lists; service is safe for concurrent dispatch

See each language's README for UML diagrams, design decisions, code flow, and MVP-scope rationale.
