# 04. The Java Memory Model

> The Java Memory Model defines legal reorderings and when writes in one thread become visible to another, making it essential for volatile, final fields, and safe publication.

## Core Concepts
### Happens-Before
If A happens-before B, B must see A’s effects unless overwritten. Key edges include program order, monitor unlock/lock, volatile write/read, thread start, successful join, and final-field initialization safety.

### Visibility and Reordering
Compilers and CPUs may reorder independent operations when single-thread semantics and JMM constraints hold. Racy code is not sequentially consistent.

### Volatile and Final
`volatile` gives visibility/order for one variable. `final` fields are safely visible after proper construction if `this` does not escape.

## How It Works Internally
Volatile and monitor operations imply memory barriers that constrain loads and stores. Safe publication can occur through static initialization, volatile references, synchronized handoff, futures/executors, thread-safe collections, or immutable objects with final fields. Broken double-checked locking without volatile can publish a reference before constructor effects are visible.

## Code Examples
```java
public final class SafePublicationDemo {
    private final int finalValue;
    private int regularValue;
    private static volatile SafePublicationDemo instance;

    private SafePublicationDemo() {
        finalValue = 42;
        regularValue = 7;
    }

    public static SafePublicationDemo getInstance() {
        SafePublicationDemo local = instance;
        if (local == null) {
            synchronized (SafePublicationDemo.class) {
                local = instance;
                if (local == null) instance = local = new SafePublicationDemo();
            }
        }
        return local;
    }
}
```

## Common Interview Questions
- **Q:** What is happens-before? **A:** A visibility and ordering relationship between actions.
- **Q:** Is volatile increment atomic? **A:** No; increment is read-modify-write.
- **Q:** What is safe publication? **A:** Publishing a reference through a happens-before edge.
- **Q:** Why final fields matter? **A:** They have initialization-safety guarantees after proper construction.
- **Q:** What is a data race? **A:** Conflicting accesses without happens-before, at least one write.
- **Q:** Does synchronized provide visibility? **A:** Yes, not just mutual exclusion.

## Pitfalls & Best Practices
- Do not rely on sleep for visibility.
- Avoid publishing `this` during construction.
- Guard shared mutable state with locks, volatile/atomic variables, or confinement.
- Prefer immutable objects for sharing.
- Reason from the JMM, not one CPU architecture.

## Related Topics
- volatile, atomic Variables & CAS
- synchronized: Intrinsic Locks & Monitors
- ThreadLocal & Thread Confinement
