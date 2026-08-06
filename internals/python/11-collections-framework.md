# 11. The Collections Framework

> Python collections hide optimized data structures behind simple APIs; interviews commonly probe list growth, dict hashing, ordering, and specialized containers.

## Core Concepts
### Built-ins
`list` is a dynamic array, `tuple` is immutable and compact, `dict` maps hashable keys to values, and `set` stores unique hashable elements.

### Dict and set tables
CPython dicts and sets use hash tables with open addressing. Dict insertion order is a language guarantee since Python 3.7.

### `collections` module
`deque`, `Counter`, `defaultdict`, `OrderedDict`, and `namedtuple` solve common container patterns cleanly.

## How It Works Internally
Lists over-allocate capacity so repeated append is amortized O(1). Dict lookup hashes a key, probes candidate slots, and uses equality to resolve collisions. Modern compact dict layout keeps dense entries in insertion order plus sparse indices. Sets use similar probing but store only keys.

## Code Examples

```python
from collections import Counter, defaultdict, deque, namedtuple

words = ['red', 'blue', 'red']
counts = Counter(words)
groups = defaultdict(list)
for word in words:
    groups[word[0]].append(word)
q = deque(['middle'])
q.appendleft('first')
q.append('last')
Point = namedtuple('Point', 'x y')
print(counts['red'])
print(dict(groups))
print(list(q))
print(Point(2, 3).x)
```

## Common Interview Questions
- **Q:** Why list append amortized O(1)? **A:** CPython over-allocates capacity.
- **Q:** Why hashable dict keys? **A:** Lookup requires stable hashes.
- **Q:** Does dict preserve order? **A:** Yes, guaranteed since Python 3.7.
- **Q:** How are collisions handled? **A:** Probing plus equality checks.
- **Q:** List vs deque as queue? **A:** `deque.popleft()` is O(1); list left pop shifts.
- **Q:** Why `defaultdict`? **A:** Centralized missing-value creation.
- **Q:** Is OrderedDict obsolete? **A:** No; it has order-specific APIs/equality.

## Pitfalls & Best Practices
- Do not mutate hash/equality-relevant key state.
- Use deque for frequent left-end operations.
- Prefer plain dict unless extra order APIs matter.
- Choose containers by operation complexity.

## Related Topics
- Python Data Model & Objects
- Copying, References & Equality
- Memory Management & Garbage Collection
