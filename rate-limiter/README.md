# Rate Limiter — LLD Machine Coding

Full MVP of a thread-safe, per-client rate limiter, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.ratelimiter.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m ratelimiter.main` |

Both suites are green (4 tests each, including a 50-threads-race-for-5-slots concurrency test) and
both demos print matching allow/deny decisions.

## What it demonstrates
- **Strategy**: `RateLimiter` abstraction with Token Bucket and Fixed Window implementations
- **Time-based algorithms**: deterministic tests through injected `Clock` / clock callable
- **Per-key state maps**: each client has independent limiter state
- **Concurrency**: per-client locks make refill/check/consume or reset/check/increment atomic

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope
rationale.
