# 07. Thread Pools & the Executor Framework

> Executors decouple task submission from thread ownership; the crucial interview details are pool sizing, queueing, rejection, factories, and shutdown.

## Core Concepts
### APIs
`Executor` executes commands. `ExecutorService` adds lifecycle and futures. `ScheduledExecutorService` supports delayed/periodic work.

### ThreadPoolExecutor Parameters
Core size, max size, keep-alive, work queue, thread factory, and rejection handler define behavior under normal load and overload.

### Queue Strategy
Unbounded queues hide overload. Bounded queues apply backpressure. `SynchronousQueue` hands off directly and can grow threads up to max.

### Shutdown
`shutdown` drains existing work; `shutdownNow` interrupts workers best-effort and returns queued tasks.

## How It Works Internally
Submission path: create workers up to core, else enqueue, else grow up to max if enqueue fails, else reject. `ThreadPoolExecutor` stores run state and worker count in an atomic control field. Built-in rejection handlers include Abort, CallerRuns, Discard, and DiscardOldest. `Executors.newFixedThreadPool` uses an unbounded queue; `newCachedThreadPool` can create too many threads.

## Code Examples
```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorDemo {
    static ExecutorService boundedPool() {
        AtomicInteger seq = new AtomicInteger();
        return new ThreadPoolExecutor(
                4, 8, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                r -> new Thread(r, "api-worker-" + seq.incrementAndGet()),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void main(String[] args) {
        ExecutorService pool = boundedPool();
        try { pool.submit(() -> System.out.println("work")); }
        finally { pool.shutdown(); }
    }
}
```

## Common Interview Questions
- **Q:** Executor vs ExecutorService? **A:** Executor only executes; ExecutorService adds submit, Future, and lifecycle.
- **Q:** Creation order in ThreadPoolExecutor? **A:** Core threads, then queue, then max threads, then reject.
- **Q:** Why avoid unbounded queues? **A:** Memory growth and hidden latency under overload.
- **Q:** CallerRunsPolicy effect? **A:** Backpressure by running the task in the submitter.
- **Q:** shutdown vs shutdownNow? **A:** Graceful drain versus best-effort interruption/drain.
- **Q:** Why ThreadFactory? **A:** Names, daemon flag, handlers, context settings.

## Pitfalls & Best Practices
- Configure production pools explicitly.
- Use bounded queues and defined rejection.
- Always shut down owned executors.
- Do not block indefinitely in shared pools.
- Name worker threads.

## Related Topics
- Futures & Async
- Concurrent Collections
- ThreadLocal & Thread Confinement
