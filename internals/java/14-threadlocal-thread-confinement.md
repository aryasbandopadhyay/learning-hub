# 14. ThreadLocal & Thread Confinement

> Thread confinement avoids synchronization by avoiding sharing; ThreadLocal gives per-thread values but is dangerous in pooled threads without cleanup.

## Core Concepts
### Thread Confinement
Data used by only one thread needs no synchronization. Examples: local variables, actor ownership, UI event-thread confinement, and worker-owned state.

### ThreadLocal
`ThreadLocal<T>` stores a separate value per thread. Common uses: trace IDs, request context, per-thread buffers, and legacy non-thread-safe formatters.

### InheritableThreadLocal
Copies values when a child thread is created; often surprising with executors because workers are created before tasks.

### Cleanup
Always call `remove()` for request-scoped values in thread pools.

## How It Works Internally
Each `Thread` owns a `ThreadLocalMap`. Keys are weak references to `ThreadLocal` objects; values are strong references. If a key is collected, a value can remain until map cleanup, so long-lived pool threads can retain large request object graphs. ThreadLocal context does not automatically cross executor or CompletableFuture boundaries.

## Code Examples
```java
public class RequestContext implements AutoCloseable {
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    static RequestContext open(String traceId) {
        TRACE_ID.set(traceId);
        return new RequestContext();
    }

    static String traceId() { return TRACE_ID.get(); }

    @Override public void close() {
        TRACE_ID.remove(); // essential for reused pool threads
    }

    static void handle(String traceId) {
        try (RequestContext ignored = RequestContext.open(traceId)) {
            System.out.println("trace=" + traceId());
        }
    }
}
```

## Common Interview Questions
- **Q:** What is confinement? **A:** Only one thread accesses the data.
- **Q:** How does ThreadLocal work? **A:** Each thread has a private map from ThreadLocal key to value.
- **Q:** Why leaks? **A:** Long-lived threads strongly retain values when not removed.
- **Q:** Why weak keys? **A:** Abandoned ThreadLocal keys can be GCed, though stale values may linger.
- **Q:** Risk in pools? **A:** Request values can leak or bleed into later tasks.
- **Q:** Does ThreadLocal propagate async? **A:** No, not automatically.

## Pitfalls & Best Practices
- Remove ThreadLocal values in finally/try-with-resources.
- Prefer explicit parameters when practical.
- Avoid storing large graphs.
- Do not assume executor propagation.
- Use confinement before shared mutable state.

## Related Topics
- The Java Memory Model
- Thread Pools & the Executor Framework
- JVM Memory & Garbage Collection
