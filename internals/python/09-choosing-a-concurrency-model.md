# 09. Choosing a Concurrency Model

> Choosing threads, processes, asyncio, or a hybrid depends on where time is spent: CPU, blocking I/O, non-blocking I/O, data sharing, and isolation.

## Core Concepts
### Threads
Best for blocking I/O, synchronous libraries, and shared-memory coordination. Limited for pure Python CPU work by the GIL.

### Processes
Best for CPU-bound Python and isolation. Costs include startup, memory, serialization, and IPC.

### Asyncio
Best for many simultaneous I/O operations when libraries are async-native and tasks await frequently.

## How It Works Internally
Threads multiplex OS threads in one interpreter process. Processes run multiple interpreters and communicate through IPC. Asyncio multiplexes many suspended coroutine frames on one loop thread. The winning model minimizes idle waiting, data copying, context switching, and conceptual complexity for the workload.

## Code Examples

```python
import asyncio
from concurrent.futures import ThreadPoolExecutor
import time

def blocking_io(i):
    time.sleep(0.01)
    return i

async def main():
    loop = asyncio.get_running_loop()
    with ThreadPoolExecutor(max_workers=2) as pool:
        results = await asyncio.gather(*(loop.run_in_executor(pool, blocking_io, i) for i in range(3)))
    print(results)

asyncio.run(main())
```

## Common Interview Questions
- **Q:** CPU-bound Python? **A:** Use processes or native/vectorized code.
- **Q:** Many async network calls? **A:** Use asyncio.
- **Q:** Legacy blocking SDK? **A:** Use threads or `to_thread`.
- **Q:** Need shared mutable state? **A:** Threads are simpler but need locks.
- **Q:** Need crash isolation? **A:** Processes help.
- **Q:** Is asyncio always faster? **A:** No; it needs non-blocking libraries.
- **Q:** Can models mix? **A:** Yes, but define boundaries clearly.

## Pitfalls & Best Practices
- Start with the simplest correct model.
- Benchmark with realistic task granularity.
- Account for serialization and startup costs.
- Avoid mixing models casually.

## Related Topics
- The GIL — Global Interpreter Lock
- Threads in Python
- Multiprocessing
- asyncio Fundamentals
