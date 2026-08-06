# 01. Process vs Thread & the Java Thread Model

> A process owns isolated memory and OS resources; a thread is a schedulable execution path inside that process. Java interviews use this to test lifecycle states, shared memory, native scheduling, and daemon semantics.

## Core Concepts
### Process vs Thread
Processes have separate address spaces; threads share heap, classes, file handles, and process resources. Each Java thread has its own call stack, program counter, and thread-local storage.

### Java Thread Model
HotSpot platform threads map roughly 1:1 to native OS threads. The OS scheduler, not Java code, decides when runnable threads execute.

### Lifecycle States
`NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, and `TERMINATED` are monitoring states, not a full OS-state model.

### Daemon Threads
The JVM exits when only daemon threads remain; daemon work is best-effort and may not finish.

## How It Works Internally
`start()` asks the JVM to create a native thread and invoke `run()` there. Calling `run()` directly is just a normal method call. Objects live on the shared heap, while local frames live on per-thread stacks. Thread priorities are platform-dependent hints. Daemon threads are not gracefully drained at shutdown, so they must not own critical cleanup.

## Code Examples
```java
public class ThreadModelDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            Thread t = Thread.currentThread();
            System.out.println(t.getName() + " daemon=" + t.isDaemon());
        }, "interview-worker");

        worker.setDaemon(false); // must be set before start()
        System.out.println(worker.getState()); // NEW
        worker.start();                       // starts a separate platform thread
        worker.join();                        // main waits for completion
        System.out.println(worker.getState()); // TERMINATED
    }
}
```

## Common Interview Questions
- **Q:** Process vs thread? **A:** A process has isolated memory/resources; threads share process memory but have independent stacks and scheduling.
- **Q:** Does `new Thread()` start execution? **A:** No. Execution starts only after `start()`.
- **Q:** `start()` vs `run()`? **A:** `start()` creates a new thread; `run()` executes in the current thread.
- **Q:** Is `RUNNABLE` always running? **A:** No. It may be ready to run or currently running.
- **Q:** When does the JVM exit? **A:** When all non-daemon threads finish.
- **Q:** Can daemon threads do critical writes? **A:** No; they may be abandoned during shutdown.

## Pitfalls & Best Practices
- Name threads for readable logs and thread dumps.
- Do not rely on priority for correctness.
- Never restart the same `Thread` object.
- Use daemon threads only for non-critical background work.
- Treat `Thread.State` as diagnostics, not synchronization.

## Related Topics
- Creating & Managing Threads
- The Java Memory Model
- Thread Pools & the Executor Framework
