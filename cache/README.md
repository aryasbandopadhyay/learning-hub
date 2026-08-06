# Cache — LLD Machine Coding

Full MVP of a thread-safe in-memory cache with pluggable eviction, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.cache.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m cache.main` |

Both implementations expose `Cache` plus the same eviction strategies: LRU and LFU.

## What it demonstrates
- **Strategy**: `EvictionPolicy` is swappable; `Cache` does not know LRU/LFU details.
- **O(1) LRU**: HashMap + doubly linked list (Python uses `OrderedDict`, the same idea).
- **O(1) LFU**: key metadata + frequency buckets, tie-broken by LRU inside a bucket.
- **Concurrency**: `get`/`put` guarded by a lock so value state and eviction metadata stay consistent.

See each language's README for UML diagrams, design-decision tables, code flow, and MVP-scope rationale.
