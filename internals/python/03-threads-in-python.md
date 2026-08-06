# 03. Threads in Python

> Python threads provide shared-memory concurrency in one process, useful for overlapping waits and integrating blocking APIs despite the GIL.

## Core Concepts
### Thread lifecycle
Create `threading.Thread`, call `start()` once, run the target, then use `join()` to wait. `is_alive()` reports whether it is still running.

### Daemon threads
Daemon threads do not keep the interpreter alive and may be stopped abruptly at shutdown. Use them only for best-effort background helpers.

### Communication
Threads share memory, so use `queue.Queue`, locks, events, or futures rather than unsynchronized globals.

## How It Works Internally
`threading` wraps OS threads and each Python thread has interpreter thread state. All threads share the process heap, modules, and file descriptors. CPython coordinates bytecode execution through the GIL, but blocking waits release it. Exceptions raised in child threads do not automatically propagate to the parent, so production code should collect results and failures explicitly.

## Code Examples

```python
import queue
import threading
import time

results = queue.Queue()

def worker(name):
    time.sleep(0.01)
    results.put((name, threading.current_thread().name))

threads = [threading.Thread(target=worker, args=(f'job-{i}',), name=f'th-{i}') for i in range(3)]
for t in threads:
    t.start()
for t in threads:
    t.join()
while not results.empty():
    print(results.get())
```

## Common Interview Questions
- **Q:** What does `start()` do? **A:** Runs the target in a new OS thread.
- **Q:** Can a thread start twice? **A:** No, that raises `RuntimeError`.
- **Q:** What does `join()` do? **A:** Waits for thread completion or timeout.
- **Q:** What is daemon mode? **A:** Daemon threads do not block process exit.
- **Q:** Do child exceptions stop main? **A:** Usually no; capture and communicate them.
- **Q:** Why use `Queue`? **A:** It is synchronized producer-consumer communication.
- **Q:** When are threads poor? **A:** Pure Python CPU parallelism.

## Pitfalls & Best Practices
- Prefer explicit shutdown over daemon reliance.
- Use queues/futures for result transfer.
- Use timeouts for robust shutdown paths.
- Keep shared-state critical sections short.

## Related Topics
- The GIL — Global Interpreter Lock
- Synchronization Primitives
- concurrent.futures
