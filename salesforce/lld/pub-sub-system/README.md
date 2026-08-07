# In-Memory Pub-Sub Message System — LLD Machine Coding

Full MVP of a thread-safe in-memory publish/subscribe broker, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.pubsub.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m pubsub.main` |

Both suites are green and both demos print identical output.

## What it demonstrates
- **OOP**: `Topic`, immutable `Message`, `Subscriber` callback abstraction, broker service
- **Observer**: subscribers register interest in a topic and are notified on publish
- **Concurrency**: `ConcurrentHashMap` / locks, bounded per-subscriber queues, background dispatchers
- **Delivery semantics**: per-subscriber offsets ensure each active subscriber receives each message once

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope rationale.
