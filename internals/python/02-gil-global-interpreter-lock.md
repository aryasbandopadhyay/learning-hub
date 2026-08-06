# 02. The GIL — Global Interpreter Lock

> The GIL is CPython mutex allowing one thread at a time to execute Python bytecode; it is central to explaining thread performance and safe interpreter internals.

## Core Concepts
### Purpose
The GIL protects interpreter state and object invariants, especially reference-count updates used throughout the C implementation.

### Performance impact
Pure Python CPU-bound threads usually contend for one bytecode-execution slot. I/O-bound threads often help because blocking I/O releases the GIL.

### Releasing the GIL
CPython releases it around many blocking system calls, and C extensions can release it while doing native work that does not touch Python objects unsafely.

## How It Works Internally
A thread executing Python bytecode owns the GIL. The interpreter periodically checks whether another thread should run, and blocking operations can drop the lock while waiting in the OS. The GIL is not a substitute for application-level synchronization: compound operations span multiple bytecodes and shared invariants still need locks, queues, or other coordination.

## Code Examples

```python
import threading
import time

def wait_job(i):
    time.sleep(0.02)  # releases the GIL while sleeping
    print(f"done {i}")

threads = [threading.Thread(target=wait_job, args=(i,)) for i in range(3)]
for t in threads:
    t.start()
for t in threads:
    t.join()
print('all joined')
```

## Common Interview Questions
- **Q:** What is the GIL? **A:** A CPython mutex allowing one thread to execute bytecode at a time.
- **Q:** Why does it exist? **A:** It simplifies object memory management and C API safety.
- **Q:** Does it protect user data? **A:** No; use synchronization for logical invariants.
- **Q:** When do threads help? **A:** I/O waits and native extensions that release the GIL.
- **Q:** How use multiple cores for CPU Python? **A:** Use processes or native/vectorized code.
- **Q:** Is it language spec? **A:** No, it is CPython-specific.
- **Q:** Can extensions release it? **A:** Yes, around safe long-running native sections.

## Pitfalls & Best Practices
- Do not expect CPU-bound thread speedup for pure Python.
- Still lock shared mutable state.
- Prefer processes for CPU-heavy Python.
- Benchmark because extension behavior matters.

## Related Topics
- Threads in Python
- Multiprocessing
- Choosing a Concurrency Model
