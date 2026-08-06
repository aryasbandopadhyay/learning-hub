# 06. concurrent.futures

> `concurrent.futures` gives a common Executor and Future API for thread and process pools, making it a favorite interview abstraction for comparing concurrency strategies.

## Core Concepts
### Executors
`ThreadPoolExecutor` runs callables in threads. `ProcessPoolExecutor` runs callables in worker processes and requires picklable tasks.

### Futures
A `Future` represents pending, running, cancelled, or finished work. `result()` returns the value or re-raises the worker exception.

### Submission patterns
`submit` gives individual futures; `map` preserves input order; `as_completed` handles results by completion order.

## How It Works Internally
Executors maintain worker pools plus internal work queues. Submitting creates a work item and a future. Worker completion stores either result or exception and wakes waiters. Thread pools share memory and the process GIL; process pools cross a serialization boundary and use multiple interpreters.

## Code Examples

```python
from concurrent.futures import ThreadPoolExecutor, as_completed
import time

def job(name, delay):
    time.sleep(delay)
    return f'{name}:{delay}'

with ThreadPoolExecutor(max_workers=3) as ex:
    futures = [ex.submit(job, 'a', 0.02), ex.submit(job, 'b', 0.01)]
    for fut in as_completed(futures):
        print(fut.result())
```

## Common Interview Questions
- **Q:** ThreadPool vs ProcessPool? **A:** Threads for I/O and shared memory; processes for CPU-bound Python.
- **Q:** What is a Future? **A:** A handle for eventual result, exception, or cancellation.
- **Q:** `map` vs `submit`? **A:** `map` is ordered; `submit` is flexible per task.
- **Q:** What is `as_completed`? **A:** Iterator yielding futures as they finish.
- **Q:** Where do exceptions appear? **A:** When calling `future.result()`.
- **Q:** Can running work be cancelled? **A:** Usually only before it starts.
- **Q:** Why context manager? **A:** It shuts down the executor predictably.

## Pitfalls & Best Practices
- Avoid unbounded submissions without backpressure.
- Do not block worker threads waiting on the same saturated pool.
- Use process pools only for picklable work.
- Size pools by workload, not habit.

## Related Topics
- Threads in Python
- Multiprocessing
- asyncio in Depth
