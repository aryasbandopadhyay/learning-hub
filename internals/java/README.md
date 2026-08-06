# Java Internals & Concurrency

| # | Topic | One-line summary |
|---:|---|---|
| 01 | [Process vs Thread & the Java Thread Model](01-process-vs-thread-the-java-thread-model.md) | Process/thread boundaries, Java platform thread mapping, lifecycle states, and daemon semantics. |
| 02 | [Creating & Managing Threads](02-creating-managing-threads.md) | Task abstractions, join, sleep/yield, and interruption-based cancellation. |
| 03 | [synchronized: Intrinsic Locks & Monitors](03-synchronized-intrinsic-locks-monitors.md) | Intrinsic monitor locking, reentrancy, wait/notify, and visibility guarantees. |
| 04 | [The Java Memory Model](04-the-java-memory-model.md) | Happens-before, visibility, reordering, volatile, final fields, and safe publication. |
| 05 | [volatile, atomic Variables & CAS](05-volatile-atomic-variables-cas.md) | Volatile visibility, atomic classes, CAS loops, LongAdder, and ABA. |
| 06 | [Explicit Locks](06-explicit-locks.md) | ReentrantLock, Conditions, ReadWriteLock, StampedLock, fairness, and AQS basics. |
| 07 | [Thread Pools & the Executor Framework](07-thread-pools-the-executor-framework.md) | Executor APIs, ThreadPoolExecutor parameters, queueing, rejection, and shutdown. |
| 08 | [Futures & Async](08-futures-async.md) | Future, FutureTask, CompletableFuture composition, and ForkJoin work stealing. |
| 09 | [Concurrent Collections](09-concurrent-collections.md) | ConcurrentHashMap, copy-on-write lists, blocking queues, and lock-free queues. |
| 10 | [Synchronizers](10-synchronizers.md) | Latch, barrier, semaphore, phaser, and exchanger coordination patterns. |
| 11 | [The Java Collections Framework](11-the-java-collections-framework.md) | Collection hierarchy, performance tradeoffs, HashMap internals, and fail-fast iterators. |
| 12 | [The Streams API](12-the-streams-api.md) | Lazy stream pipelines, collectors, terminal operations, and parallel stream guidance. |
| 13 | [Deadlock, Livelock & Starvation](13-deadlock-livelock-starvation.md) | Progress failure causes, diagnostics, and prevention techniques. |
| 14 | [ThreadLocal & Thread Confinement](14-threadlocal-thread-confinement.md) | Per-thread state, confinement, context cleanup, and pool leak risks. |
| 15 | [JVM Memory & Garbage Collection](15-jvm-memory-garbage-collection.md) | Heap/stack/metaspace, references, allocation, GC roots, G1, and ZGC. |
| 16 | [Class Loading & JVM Execution](16-class-loading-jvm-execution.md) | Class-loader delegation, linking, initialization, bytecode, JIT, and deoptimization. |
