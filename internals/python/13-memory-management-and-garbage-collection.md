# 13. Memory Management & Garbage Collection

> CPython memory management combines immediate reference counting with cyclic garbage collection, explaining object lifetime, cycles, weak references, and memory tuning.

## Core Concepts
### Reference counting
Every CPython object tracks strong references. When the count reaches zero, deallocation usually happens immediately.

### Cyclic GC
Reference counting cannot reclaim cycles, so CPython tracks container objects and periodically detects groups reachable only from each other.

### Memory tools
`__slots__` can reduce per-instance overhead, `weakref` avoids extending lifetime, and `tracemalloc` attributes allocations.

## How It Works Internally
CPython uses specialized allocators for small objects and type-specific free lists. The cyclic GC separates tracked objects into generations so young containers are scanned more frequently. `del` removes a reference, not necessarily an object. Finalizers and cycles can make destruction timing subtle, so explicit context management is preferred for external resources.

## Code Examples

```python
import gc
import weakref

class Node:
    __slots__ = ('name', 'child', '__weakref__')
    def __init__(self, name):
        self.name = name
        self.child = None

a = Node('a')
b = Node('b')
a.child = b
b.child = a
ref = weakref.ref(a)
del a, b
print(gc.collect() >= 0)
print(ref() is None)
```

## Common Interview Questions
- **Q:** Primary CPython reclamation? **A:** Reference counting.
- **Q:** Why cyclic GC? **A:** Cycles can keep reference counts nonzero.
- **Q:** What does GC track? **A:** Mostly containers that can form cycles.
- **Q:** What does `__slots__` do? **A:** Declares fixed attributes and can remove instance dict.
- **Q:** Why weakref? **A:** Caches/back-links that should not keep objects alive.
- **Q:** Does `del` delete object? **A:** It deletes a reference.
- **Q:** How manage files? **A:** Use context managers, not GC timing.

## Pitfalls & Best Practices
- Do not rely on `__del__` for critical cleanup.
- Use `with` for external resources.
- Profile before optimizing memory.
- Use weak refs for parent/back links when appropriate.

## Related Topics
- The CPython Execution Model
- Copying, References & Equality
- Python Data Model & Objects
