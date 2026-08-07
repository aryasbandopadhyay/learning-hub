# Custom HashMap — LLD Machine Coding

Full MVP of a from-scratch hash map, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java; & $mvn -o test` | `java -cp target\classes com.example.hashmap.Main` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python; python -m pytest -q` | `python -m hashmap.main` |

Both implementations avoid built-in maps/dicts for storage. They use an array/list of buckets,
separate chaining for collisions, and dynamic resizing when `size / capacity` exceeds `0.75`.

## What it demonstrates
- **OOP / generics**: Java `MyHashMap<K,V>` and Python `MyHashMap[K,V]`
- **Hashing**: hash spreading + power-of-two bucket index `hash & (capacity - 1)`
- **Collision handling**: linked-list chains inside buckets
- **Resizing**: capacity doubles and all nodes are rehashed, with tests proving keys survive
- **Trade-offs**: single-threaded MVP, with concurrency extension notes in each README

See each language's README for UML diagrams, design decisions, code flow, and test details.
