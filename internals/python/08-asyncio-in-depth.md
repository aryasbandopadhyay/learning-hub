# 08. asyncio in Depth

> Advanced asyncio covers awaitables, async synchronization, executor bridges, cancellation, and mistakes that silently remove concurrency.

## Core Concepts
### Awaitables
Coroutines, Tasks, and Futures are awaitable. Creating tasks lets several coroutines make progress concurrently.

### Async primitives
`asyncio.Lock`, `Event`, `Condition`, `Semaphore`, and `Queue` coordinate coroutines without blocking the OS thread.

### Sync bridges
Use `asyncio.to_thread` or `run_in_executor` for blocking functions; use processes for CPU-bound Python.

## How It Works Internally
Asyncio futures store results and continuation callbacks. Awaiting a future registers the current task to be resumed later. Async locks queue tasks rather than parking OS threads. Cancellation injects `CancelledError` at an await point, so `finally` blocks and async context managers are important for cleanup.

## Code Examples

```python
import asyncio
import time

def blocking(value):
    time.sleep(0.01)
    return value * 2

async def worker(i, sem):
    async with sem:
        await asyncio.sleep(0.01)
        return i

async def main():
    sem = asyncio.Semaphore(2)
    doubled = await asyncio.to_thread(blocking, 21)
    values = await asyncio.gather(*(worker(i, sem) for i in range(4)))
    print(doubled, values)

asyncio.run(main())
```

## Common Interview Questions
- **Q:** Coroutine vs Task? **A:** A coroutine is awaitable code; a Task schedules it.
- **Q:** What is Future? **A:** Low-level event-loop result placeholder.
- **Q:** Why `asyncio.Queue`? **A:** Non-blocking producer-consumer coordination.
- **Q:** `to_thread` use? **A:** Run blocking I/O off the loop thread.
- **Q:** Can `threading.Lock` block async code? **A:** Yes; prefer asyncio primitives in coroutines.
- **Q:** Accidental sequential async? **A:** Awaiting each operation immediately instead of scheduling first.
- **Q:** Cancellation mechanism? **A:** `CancelledError` appears at await points.

## Pitfalls & Best Practices
- Never do blocking I/O on the event loop.
- Create tasks before awaiting when concurrency is intended.
- Use async context managers for locks and semaphores.
- Do not swallow cancellation accidentally.

## Related Topics
- asyncio Fundamentals
- Synchronization Primitives
- concurrent.futures
