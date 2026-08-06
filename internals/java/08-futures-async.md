# 08. Futures & Async

> Futures model asynchronous results, while CompletableFuture adds non-blocking composition; understanding pools and exception flow is key in interviews.

## Core Concepts
### Future and FutureTask
`Future` exposes `get`, cancellation, and status. `FutureTask` adapts `Callable`/`Runnable` into an executable task that stores completion.

### CompletableFuture
Supports transformations (`thenApply`), flattening (`thenCompose`), combining (`thenCombine`), async variants, and recovery (`exceptionally`, `handle`).

### ForkJoinPool
Fork/join targets divide-and-conquer. Workers maintain deques and idle workers steal work from busy workers.

## How It Works Internally
`Future.get` blocks and wraps failures in `ExecutionException`; `CompletableFuture.join` throws unchecked `CompletionException`. Non-async continuations may run in the completing thread. Async methods without an explicit executor usually use the common ForkJoinPool. Work stealing pops local tasks LIFO and steals FIFO from another worker, balancing load while keeping locality.

## Code Examples
```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        ExecutorService io = Executors.newFixedThreadPool(4);
        try {
            CompletableFuture<Integer> score = CompletableFuture
                    .supplyAsync(() -> "arya", io)
                    .thenApply(String::toUpperCase)
                    .thenCompose(n -> CompletableFuture.supplyAsync(() -> n.length() * 10, io))
                    .exceptionally(ex -> 0);
            System.out.println(score.join());
        } finally { io.shutdown(); }
    }
}
```

## Common Interview Questions
- **Q:** Future vs CompletableFuture? **A:** Future is blocking/result retrieval; CompletableFuture supports completion pipelines.
- **Q:** thenApply vs thenCompose? **A:** Map a value versus flatten a future-producing function.
- **Q:** thenCombine? **A:** Combine independent async results.
- **Q:** get vs join? **A:** Checked ExecutionException versus unchecked CompletionException.
- **Q:** What does cancel do? **A:** Attempts cancellation; running work must cooperate with interruption.
- **Q:** What is work stealing? **A:** Idle workers steal queued tasks from other workers.

## Pitfalls & Best Practices
- Use explicit executors for blocking work.
- Prefer composition over nested get calls.
- Handle exceptions intentionally.
- Avoid blocking in the common pool.
- Keep fork/join tasks non-blocking and appropriately sized.

## Related Topics
- Thread Pools & the Executor Framework
- The Streams API
- Creating & Managing Threads
