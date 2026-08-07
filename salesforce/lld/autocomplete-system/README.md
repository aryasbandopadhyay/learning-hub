# Trie Autocomplete System — LLD Machine Coding

Full MVP of a Trie-backed typeahead system, implemented **twice** with an identical design:

| Language | Location | Run tests | Run demo |
| --- | --- | --- | --- |
| Java (Maven, JUnit 5) | [`java/`](./java/README.md) | `cd java && mvn test` | `mvn -q compile exec:java "-Dexec.mainClass=com.example.autocomplete.Main"` |
| Python (pytest) | [`python/`](./python/README.md) | `cd python && python -m pytest -q` | `python -m autocomplete.main` |

Both suites are green and both demos print identical output.

## What it demonstrates
- **Trie prefix walk**: follow one edge per prefix character before collecting matches
- **Ranking**: frequency descending, then lexicographic ascending for ties
- **Top-k heap**: keep only the best `k` candidates instead of sorting the entire corpus first
- **Interactive LC642 mode**: `input(char)` buffers characters until `#`, then records the sentence
- **Extension-ready design**: single-threaded MVP, with thread-safety called out as a follow-up

See each language's README for UML diagrams, design-decision tables, code flow, and the MVP-scope
rationale.
