# 15. JVM Memory & Garbage Collection

> JVM memory and GC knowledge explains performance, leaks, pauses, references, and object lifetime in real Java systems.

## Core Concepts
### Memory Areas
Heap stores objects. Each thread has a Java stack with frames, locals, and operand stack. Metaspace stores class metadata in native memory. The code cache stores JIT-compiled code.

### Generational Hypothesis
Most objects die young, so collectors optimize for frequent young-object reclamation.

### References
Strong references keep objects alive. Soft references are memory-sensitive. Weak references do not prevent collection. Phantom references support post-mortem cleanup.

### Finalization
Finalization is deprecated for removal; prefer try-with-resources, explicit close, or Cleaner.

## How It Works Internally
Most small allocations use thread-local allocation buffers and pointer bumping. GC starts from roots such as stacks, static fields, JNI handles, and VM structures. G1 uses regions, concurrent marking, evacuation, and pause goals. ZGC targets very low pauses with colored pointers/load barriers and concurrent relocation. All collectors still have some stop-the-world phases.

## Code Examples
```java
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MemoryDemo {
    static class Payload { byte[] data = new byte[1024 * 1024]; }

    public static void main(String[] args) {
        Payload strong = new Payload();
        WeakReference<Payload> weak = new WeakReference<>(strong);
        System.out.println(weak.get() != null); // strongly reachable
        strong = null; // now eligible when only weakly reachable

        List<byte[]> heapObjects = new ArrayList<>();
        heapObjects.add(new byte[1024]); // object data is on the heap
    }
}
```

## Common Interview Questions
- **Q:** Heap vs stack? **A:** Objects on shared heap; per-thread stacks hold frames and locals/references.
- **Q:** What is metaspace? **A:** Native memory for class metadata.
- **Q:** What are GC roots? **A:** Starting points for reachability: stacks, statics, JNI, VM internals, etc.
- **Q:** Why generational GC? **A:** Most objects die young.
- **Q:** G1 goal? **A:** Predictable pauses with region-based collection.
- **Q:** ZGC goal? **A:** Very low pause times on large heaps.
- **Q:** Why avoid finalizers? **A:** Unpredictable, slow, resurrection-prone, deprecated.

## Pitfalls & Best Practices
- Close external resources explicitly.
- Treat leaks as unwanted reachability.
- Use GC logs and heap dumps for evidence.
- Avoid finalizers.
- Tune GC after measurement, not guesswork.

## Related Topics
- ThreadLocal & Thread Confinement
- Class Loading & JVM Execution
- The Java Collections Framework
