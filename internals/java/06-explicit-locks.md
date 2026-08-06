# 06. Explicit Locks

> Explicit locks add timed, interruptible, fair, multi-condition, read/write, and optimistic locking options beyond synchronized.

## Core Concepts
### ReentrantLock
Like synchronized but with `tryLock`, timed acquisition, `lockInterruptibly`, fairness, and `Condition`.

### Condition
A condition is a separate wait queue tied to one lock. Use `await`, `signal`, and `signalAll` while holding the lock.

### ReadWriteLock
Allows many readers or one writer, useful for read-heavy structures.

### StampedLock
Provides write, read, and optimistic-read stamps; it is not reentrant.

### Fairness
Fair locks reduce starvation but usually lower throughput.

## How It Works Internally
Many explicit synchronizers are based on `AbstractQueuedSynchronizer` (AQS), which uses a volatile state field, CAS, a FIFO wait queue, and `LockSupport.park/unpark`. `Condition.signal` transfers nodes from a condition queue to the lock queue. `StampedLock` optimistic reads validate that no write occurred after the stamp was obtained.

## Code Examples
```java
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockBuffer<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final Queue<T> q = new ArrayDeque<>();
    private final int capacity;
    public LockBuffer(int capacity) { this.capacity = capacity; }

    public void put(T item) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (q.size() == capacity) notFull.await();
            q.add(item);
            notEmpty.signal();
        } finally { lock.unlock(); }
    }
}
```

## Common Interview Questions
- **Q:** Why ReentrantLock? **A:** Timed/interruptible acquisition, fairness, and multiple conditions.
- **Q:** What must follow lock()? **A:** unlock in a finally block.
- **Q:** Condition vs wait/notify? **A:** Multiple condition queues per lock versus one wait set per monitor.
- **Q:** Fair lock tradeoff? **A:** Less starvation, often lower throughput.
- **Q:** ReadWriteLock use case? **A:** Many reads, few writes, meaningful read duration.
- **Q:** Is StampedLock reentrant? **A:** No.

## Pitfalls & Best Practices
- Always unlock in finally.
- Prefer nonfair locks unless starvation matters.
- Use separate conditions for separate predicates.
- Do not call Condition methods without holding the lock.
- Validate StampedLock optimistic reads.

## Related Topics
- synchronized: Intrinsic Locks & Monitors
- Synchronizers
- Deadlock, Livelock & Starvation
