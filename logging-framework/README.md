# Logging Framework — LLD Machine Coding

Full MVP of a thread-safe logging framework, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.logging.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m logkit.main` |

Both implementations expose the same concepts: `LogLevel`, `LogRecord`, `Logger`, `LogManager`,
`Appender`, and `Formatter`. The Python package is named `logkit` so it does not shadow the standard
library `logging` module.

## What it demonstrates
- **Singleton + Factory**: `LogManager.getLogger(name)` caches and returns named loggers
- **Strategy**: pluggable `Formatter` implementations used by appenders
- **Fan-out / Observer-like chain**: one logger publishes each record to multiple appenders
- **Concurrency**: appenders guard writes with `synchronized` / `Lock`, and tests prove no records are
  lost when many threads log at once
- **Testability**: an injected clock makes timestamps deterministic

See each language's README for UML diagrams, design-decision tables, code flow, run steps, and the
MVP-scope rationale.
