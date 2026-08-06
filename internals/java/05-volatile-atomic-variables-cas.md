# 05. volatile, atomic Variables & CAS

> Volatile and atomic classes are lightweight concurrency tools for visibility and simple atomic transitions; CAS explains how much of java.util.concurrent scales without coarse locks.

## Core Concepts
### Volatile
A volatile write happens-before a later volatile read of the same variable. Use it for flags or published references, not compound invariants.

### Atomic Variables
`AtomicInteger`, `AtomicLong`, and `AtomicReference` provide atomic read-modify-write operations such as `compareAndSet` and `getAndUpdate`.

### CAS
Compare-and-set updates a value only if it equals an expected value; failures normally retry.

### LongAdder and ABA
`LongAdder` stripes hot counters across cells. ABA occurs when A changes to B and back to A, fooling simple CAS.

## How It Works Internally
Atomic classes use JVM intrinsics/VarHandles to emit hardware atomic instructions. Atomic reads/writes have volatile-like memory effects; successful CAS has both read and write effects. Under contention, one CAS location causes cache-line bouncing, so `LongAdder` distributes updates. ABA is mitigated with versions, `AtomicStampedReference`, or immutable state transitions.

## Code Examples
```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAdder;

public class AtomicDemo {
    private final AtomicInteger state = new AtomicInteger();
    private final LongAdder requests = new LongAdder();

    boolean startOnce() {
        return state.compareAndSet(0, 1); // one winner
    }

    void recordRequest() { requests.increment(); }
    long requestCount() { return requests.sum(); }

    static void stamped() {
        AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
        int[] stamp = new int[1];
        String old = ref.get(stamp);
        ref.compareAndSet(old, "B", stamp[0], stamp[0] + 1);
    }
}
```

## Common Interview Questions
- **Q:** When use volatile? **A:** For independent visibility flags or safe reference publication.
- **Q:** Why is volatile count++ unsafe? **A:** Increment has separate read, add, and write steps.
- **Q:** What is CAS? **A:** Atomic conditional update based on an expected current value.
- **Q:** AtomicInteger vs LongAdder? **A:** AtomicInteger is exact single-cell CAS; LongAdder scales counters but sum is not a linearizable snapshot.
- **Q:** What is ABA? **A:** A value changes A->B->A, hiding a change from CAS.
- **Q:** Are atomics always faster than locks? **A:** Not under all contention or invariant complexity.

## Pitfalls & Best Practices
- Do not coordinate multi-field invariants with unrelated atomics.
- Use LongAdder for hot metrics, AtomicLong for exact CAS semantics.
- Beware spin retry storms under contention.
- Prefer immutable state in AtomicReference.
- Document low-level concurrency assumptions.

## Related Topics
- The Java Memory Model
- Concurrent Collections
- Synchronizers
