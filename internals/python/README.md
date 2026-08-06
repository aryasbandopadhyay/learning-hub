# Python Internals & Concurrency Interview Notes

| # | Topic | One-line summary |
|---|---|---|
| 01 | [The CPython Execution Model](01-cpython-execution-model.md) | How CPython compiles source to bytecode, executes frames, caches .pyc files, and uses reference counts. |
| 02 | [The GIL — Global Interpreter Lock](02-gil-global-interpreter-lock.md) | What the GIL protects, why it exists, and how it affects CPU-bound and I/O-bound work. |
| 03 | [Threads in Python](03-threads-in-python.md) | Thread lifecycle, daemon behavior, joining, queues, and when threads help despite the GIL. |
| 04 | [Synchronization Primitives](04-synchronization-primitives.md) | Locks, events, conditions, semaphores, barriers, and deadlock avoidance. |
| 05 | [Multiprocessing](05-multiprocessing.md) | Processes, spawn vs fork, IPC, pools, shared memory, managers, and pickling constraints. |
| 06 | [concurrent.futures](06-concurrent-futures.md) | Executor and Future abstractions for thread and process pools. |
| 07 | [asyncio Fundamentals](07-asyncio-fundamentals.md) | Event loops, coroutines, tasks, gather, and cooperative scheduling basics. |
| 08 | [asyncio in Depth](08-asyncio-in-depth.md) | Awaitables, async synchronization, executor bridges, cancellation, and blocking mistakes. |
| 09 | [Choosing a Concurrency Model](09-choosing-a-concurrency-model.md) | A workload-based guide to threads, processes, asyncio, and hybrids. |
| 10 | [Python Data Model & Objects](10-python-data-model-and-objects.md) | Identity, type, value, mutability, and dunder protocol fundamentals. |
| 11 | [The Collections Framework](11-collections-framework.md) | List, tuple, dict, set internals plus key containers from collections. |
| 12 | [Iterators, Generators & Coroutines](12-iterators-generators-and-coroutines.md) | Iterator protocol, generator frames, yield, laziness, and pipelines. |
| 13 | [Memory Management & Garbage Collection](13-memory-management-and-garbage-collection.md) | Reference counting, cyclic GC, generations, __slots__, weakrefs, and profiling. |
| 14 | [Copying, References & Equality](14-copying-references-and-equality.md) | Assignment, shallow/deep copy, identity, equality, and interning. |
| 15 | [Decorators, Closures & Scope](15-decorators-closures-and-scope.md) | LEGB, closure cells, nonlocal, and practical decorator patterns. |
