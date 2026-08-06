# 09. Concurrent Collections

> Concurrent collections encode thread-safe access patterns with scalability, weakly consistent iteration, and optional blocking behavior.

## Core Concepts
### ConcurrentHashMap
Supports concurrent reads and high-concurrency updates; `null` keys/values are disallowed to keep absence unambiguous.

### CopyOnWriteArrayList
Reads use immutable array snapshots; each write copies the whole array. Excellent for listener lists.

### BlockingQueue
Producer-consumer abstraction with blocking `put`/`take`. Common types: `ArrayBlockingQueue`, `LinkedBlockingQueue`, `PriorityBlockingQueue`, `DelayQueue`, `SynchronousQueue`, and `LinkedTransferQueue`.

### ConcurrentLinkedQueue
A non-blocking unbounded FIFO queue based on linked nodes and CAS.

## How It Works Internally
Java 8+ `ConcurrentHashMap` uses a table of bins. Empty-bin insertion uses CAS; contended bins synchronize on the first node; long collision chains can become red-black trees. Resizing is cooperative. Iterators are weakly consistent, not fail-fast. `CopyOnWriteArrayList` publishes a new backing array after each mutation. Blocking queues use locks/conditions or transfer algorithms depending on type.

## Code Examples
```java
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentCollectionsDemo {
    private final ConcurrentHashMap<String, Long> counts = new ConcurrentHashMap<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(100);

    void increment(String key) { counts.merge(key, 1L, Long::sum); }
    void addListener(Runnable r) { listeners.add(r); }
    void publish(String item) throws InterruptedException { queue.put(item); }
    String consume() throws InterruptedException { return queue.take(); }
}
```

## Common Interview Questions
- **Q:** Why no nulls in ConcurrentHashMap? **A:** So null from get means no mapping.
- **Q:** Are its iterators fail-fast? **A:** No, they are weakly consistent.
- **Q:** When use CopyOnWriteArrayList? **A:** Many reads, rare writes, small/medium lists.
- **Q:** ArrayBlockingQueue vs LinkedBlockingQueue? **A:** Fixed array/single lock versus linked nodes and often separate put/take locks.
- **Q:** What is SynchronousQueue? **A:** Zero-capacity handoff between producer and consumer.
- **Q:** Is ConcurrentLinkedQueue blocking? **A:** No; empty poll returns null.

## Pitfalls & Best Practices
- Use atomic map methods like merge/compute.
- Avoid mutable keys.
- Avoid copy-on-write for write-heavy data.
- Prefer bounded queues for backpressure.
- Do not expect consistent snapshots from weak iterators.

## Related Topics
- volatile, atomic Variables & CAS
- Thread Pools & the Executor Framework
- The Java Collections Framework
