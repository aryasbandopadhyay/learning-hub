# 10. Synchronizers

> Synchronizers provide reusable coordination patterns that are safer and clearer than hand-written wait/notify protocols.

## Core Concepts
### CountDownLatch
One-shot gate; waiters proceed when the count reaches zero.

### CyclicBarrier
Reusable barrier for a fixed number of parties, with optional barrier action.

### Semaphore
Controls a number of permits for throttling or resource pools.

### Phaser
Multi-phase barrier with dynamic registration.

### Exchanger
Rendezvous where two threads swap values.

## How It Works Internally
`CountDownLatch` and `Semaphore` are AQS-based shared synchronizers with a volatile state representing count or permits. `CyclicBarrier` uses a lock, condition, count, and generation; interruption or timeout breaks the generation. `Phaser` packs phase, registered parties, and unarrived count in state. Actions before `countDown` happen-before actions after a successful `await`.

## Code Examples
```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class SynchronizerDemo {
    private final Semaphore permits = new Semaphore(3, true);

    void callRemote() throws InterruptedException {
        permits.acquire();
        try { System.out.println("at most three callers"); }
        finally { permits.release(); }
    }

    static void startTogether() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        Runnable r = () -> { ready.countDown(); try { start.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } };
        new Thread(r).start(); new Thread(r).start();
        ready.await();
        start.countDown();
    }
}
```

## Common Interview Questions
- **Q:** Latch vs barrier? **A:** Latch is one-shot count-down; barrier is reusable and waits for parties to arrive.
- **Q:** Semaphore use? **A:** Limit concurrent access to a scarce resource.
- **Q:** Can Semaphore be fair? **A:** Yes, with throughput cost.
- **Q:** When Phaser? **A:** Dynamic parties and repeated phases.
- **Q:** Broken CyclicBarrier? **A:** Waiters fail when a party is interrupted/times out; generation is broken.
- **Q:** What does Exchanger do? **A:** Pairs two threads and swaps objects.

## Pitfalls & Best Practices
- Release semaphore permits in finally.
- Use timeouts for failure-prone coordination.
- Do not try to reset CountDownLatch.
- Prefer high-level synchronizers over custom monitor code.
- Understand interruption effects on barriers.

## Related Topics
- Explicit Locks
- Thread Pools & the Executor Framework
- Deadlock, Livelock & Starvation
