# 11. The Java Collections Framework

> Collections are everyday interview material: hierarchy, performance, HashMap internals, ordering, and iterator behavior all matter.

## Core Concepts
### Hierarchy
`Collection` branches into `List`, `Set`, and `Queue/Deque`; `Map` is separate for key-value associations.

### Lists
`ArrayList` is a resizable array with O(1) indexed access and amortized append. `LinkedList` is a doubly linked list with O(n) indexing and high node overhead.

### Maps and Sets
`HashSet` wraps `HashMap`. `TreeMap`/`TreeSet` are sorted red-black trees. `LinkedHashMap` preserves insertion order or access order.

### Iterators
Most mutable non-concurrent iterators are fail-fast on best effort.

## How It Works Internally
`HashMap` uses a power-of-two bucket array and indexes with `(capacity - 1) & spreadHash`. Java 8+ treeifies a bucket at 8 entries when capacity is at least 64; otherwise it resizes first. Untreeify happens around 6. Default load factor is 0.75. Resize doubles capacity. `TreeMap` operations are O(log n). `LinkedHashMap` adds a doubly linked list through entries. Fail-fast uses `modCount` checks.

## Code Examples
```java
import java.util.*;

public class CollectionsDemo {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Ada");
        names.add("Grace");

        Map<Integer, String> lru = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > 2;
            }
        };
        lru.put(1, "one"); lru.put(2, "two"); lru.get(1); lru.put(3, "three");
        System.out.println(lru); // key 2 was least recently used
    }
}
```

## Common Interview Questions
- **Q:** ArrayList vs LinkedList? **A:** ArrayList has fast indexing/cache locality; LinkedList has O(n) indexing and more memory overhead.
- **Q:** HashMap collisions? **A:** List bins first, red-black tree bins after threshold/capacity conditions.
- **Q:** Treeify threshold? **A:** 8 entries when capacity is at least 64.
- **Q:** Default load factor? **A:** 0.75.
- **Q:** TreeMap complexity? **A:** O(log n) for get/put/remove.
- **Q:** Fail-fast guaranteed? **A:** No, best-effort bug detection.

## Pitfalls & Best Practices
- Use ArrayList by default for lists.
- Pre-size large HashMaps when size is known.
- Do not mutate fields used by key equals/hashCode.
- Use Iterator.remove during iteration.
- Use concurrent collections for concurrent mutation.

## Related Topics
- Concurrent Collections
- The Streams API
- JVM Memory & Garbage Collection
