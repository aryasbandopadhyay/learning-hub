# 03. synchronized: Intrinsic Locks & Monitors

> `synchronized` is Java’s built-in mutual exclusion and condition waiting mechanism, combining locking with memory visibility guarantees.

## Core Concepts
### Intrinsic Locks
Every object can be a monitor. Instance synchronized methods lock `this`; static synchronized methods lock the `Class` object; blocks lock the object in parentheses.

### Reentrancy
A thread that owns a monitor can enter it again; the JVM tracks a hold count.

### Wait/Notify
`wait()` releases the monitor and enters the wait set. `notify` wakes one waiter; `notifyAll` wakes all. Woken threads must reacquire the monitor.

### Visibility
Unlock happens-before a later lock of the same monitor.

## How It Works Internally
HotSpot stores lock state in the object header mark word and may inflate contended monitors to monitor objects. Threads trying to enter are distinct from threads in the wait set. Spurious wakeups are permitted, so condition predicates must be checked in a loop. `notifyAll` is safer when different predicates share a monitor.

## Code Examples
```java
import java.util.ArrayDeque;
import java.util.Queue;

public class BoundedBuffer<T> {
    private final Queue<T> q = new ArrayDeque<>();
    private final int capacity;
    public BoundedBuffer(int capacity) { this.capacity = capacity; }

    public synchronized void put(T item) throws InterruptedException {
        while (q.size() == capacity) wait(); // releases this monitor
        q.add(item);
        notifyAll(); // consumers may proceed
    }

    public synchronized T take() throws InterruptedException {
        while (q.isEmpty()) wait();
        T item = q.remove();
        notifyAll(); // producers may proceed
        return item;
    }
}
```

## Common Interview Questions
- **Q:** Instance vs static synchronized lock? **A:** Instance locks `this`; static locks `ClassName.class`.
- **Q:** What is reentrancy? **A:** The owning thread can reacquire the same lock.
- **Q:** Does wait release the monitor? **A:** Yes, then reacquires before returning.
- **Q:** Does notify release immediately? **A:** No, only when the notifier exits synchronized code.
- **Q:** Why wait in a while loop? **A:** For spurious wakeups and changed predicates.
- **Q:** Memory effect? **A:** Unlock happens-before later lock on the same monitor.

## Pitfalls & Best Practices
- Synchronize on private final lock objects when possible.
- Keep critical sections focused.
- Prefer `notifyAll` unless `notify` is proven correct.
- Never call wait/notify outside synchronized code for that object.
- Avoid blocking I/O while holding monitors.

## Related Topics
- The Java Memory Model
- Explicit Locks
- Deadlock, Livelock & Starvation
