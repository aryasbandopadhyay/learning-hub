# 13. Deadlock, Livelock & Starvation

> Progress failures test whether you can reason beyond data races: deadlock, livelock, and starvation require diagnosis plus prevention patterns.

## Core Concepts
### Deadlock
Threads wait forever in a cycle. Coffman conditions: mutual exclusion, hold-and-wait, no preemption, and circular wait.

### Livelock
Threads keep reacting and running but make no useful progress.

### Starvation
One thread cannot make progress because resources are repeatedly given to others.

### Prevention
Use global lock ordering, avoid nested locks, use timeouts, acquire all locks together, reduce lock scope, and avoid external calls while locked.

### Detection
Thread dumps and `ThreadMXBean.findDeadlockedThreads()` identify many Java monitor/ownable-synchronizer deadlocks.

## How It Works Internally
A wait-for graph models threads and resources; a cycle indicates deadlock. HotSpot thread dumps can report monitor and `java.util.concurrent` ownable-synchronizer deadlocks. Fair locks reduce starvation risk at throughput cost. `tryLock(timeout)` can break circular wait by releasing already-held locks, backing off, and retrying.

## Code Examples
```java
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockAvoidanceDemo {
    static boolean acquireBoth(ReentrantLock a, ReentrantLock b) throws InterruptedException {
        while (true) {
            if (a.tryLock(50, TimeUnit.MILLISECONDS)) {
                try {
                    if (b.tryLock(50, TimeUnit.MILLISECONDS)) {
                        try { return true; }
                        finally { b.unlock(); }
                    }
                } finally { a.unlock(); }
            }
            Thread.sleep(10); // backoff; production code may add jitter
        }
    }

    static long[] deadlockedThreads() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.findDeadlockedThreads();
    }
}
```

## Common Interview Questions
- **Q:** Four deadlock conditions? **A:** Mutual exclusion, hold-and-wait, no preemption, circular wait.
- **Q:** How lock ordering helps? **A:** All threads acquire locks in one global order, preventing cycles.
- **Q:** Deadlock vs livelock? **A:** Blocked forever versus active but not progressing.
- **Q:** Starvation vs deadlock? **A:** System progresses but one thread does not versus a cycle stuck forever.
- **Q:** How tryLock helps? **A:** Timeout lets code release and retry instead of waiting forever.
- **Q:** How diagnose? **A:** Thread dumps, JFR, jcmd/jstack, or ThreadMXBean.

## Pitfalls & Best Practices
- Document lock ordering.
- Do not call unknown code while holding locks.
- Use timeouts across components.
- Separate pools to prevent thread-starvation deadlocks.
- Capture diagnostics before restart.

## Related Topics
- Explicit Locks
- synchronized: Intrinsic Locks & Monitors
- Thread Pools & the Executor Framework
