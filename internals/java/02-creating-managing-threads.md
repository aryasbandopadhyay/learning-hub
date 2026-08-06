# 02. Creating & Managing Threads

> Thread creation is simple, but correct management requires understanding task abstractions, joining, sleeping, yielding, and interruption-based cancellation.

## Core Concepts
### Creation Options
`Thread` is the execution carrier. `Runnable` is a no-result task. `Callable<V>` returns a value and can throw checked exceptions, normally through an executor or `FutureTask`.

### Coordination
`join()` waits for a thread to terminate. `sleep()` pauses the current thread but does not release locks. `yield()` is only a scheduler hint.

### Interruption
`interrupt()` is cooperative cancellation. Blocking methods often throw `InterruptedException` and clear the interrupt flag; code should stop, propagate, or restore the flag.

## How It Works Internally
Every thread has an interrupt status bit. `sleep`, `wait`, `join`, and many blocking `java.util.concurrent` methods observe it. `join()` internally waits on the target thread object until the JVM marks it terminated. Raw `Thread` cannot return values; `FutureTask` stores result, exception, or cancellation state around a `Callable`.

## Code Examples
```java
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ManagingThreadsDemo {
    public static void main(String[] args) throws Exception {
        Runnable printer = () -> System.out.println("no direct result");
        Thread t = new Thread(printer, "printer");
        t.start();
        t.join();

        Callable<Integer> sum = () -> {
            int total = 0;
            for (int i = 1; i <= 100; i++) {
                if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                total += i;
            }
            return total;
        };
        FutureTask<Integer> task = new FutureTask<>(sum);
        new Thread(task, "calculator").start();
        System.out.println(task.get());
    }
}
```

## Common Interview Questions
- **Q:** Runnable vs Callable? **A:** Runnable has no result/checked exception; Callable returns a value and can throw checked exceptions.
- **Q:** What does join do? **A:** Blocks the caller until the target thread terminates or timeout elapses.
- **Q:** Does sleep release locks? **A:** No.
- **Q:** Is yield reliable? **A:** No; it is only a hint.
- **Q:** Why restore interrupt status? **A:** Catching `InterruptedException` clears it; restoring lets outer code observe cancellation.
- **Q:** Can Thread be started twice? **A:** No, a second start throws `IllegalThreadStateException`.

## Pitfalls & Best Practices
- Prefer executors over manually creating many threads.
- Do not swallow `InterruptedException`.
- Avoid deprecated `stop`, `suspend`, and `resume`.
- Do not use sleep for coordination.
- Make cancellation cleanup idempotent.

## Related Topics
- Process vs Thread & the Java Thread Model
- Thread Pools & the Executor Framework
- Futures & Async
