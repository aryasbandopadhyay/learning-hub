# 07. asyncio Fundamentals

> `asyncio` provides single-threaded cooperative concurrency through event loops, coroutines, tasks, and `await`; it is essential for modern Python I/O interviews.

## Core Concepts
### Coroutines
Calling an `async def` function returns a coroutine object; it does not execute until awaited or scheduled.

### Event loop
The event loop runs tasks, callbacks, timers, and I/O readiness notifications on one thread.

### Tasks and gather
A Task schedules a coroutine. `asyncio.gather` awaits several awaitables and returns results in input order.

## How It Works Internally
An `await` point suspends the current coroutine and returns control to the event loop. The coroutine resumes when the awaited object completes. Coroutine objects keep suspended frame state similar to generators. Scheduling is cooperative, so CPU-bound code or blocking calls that do not await stop all tasks on that loop.

## Code Examples

```python
import asyncio

async def fetch(name, delay):
    await asyncio.sleep(delay)
    return name.upper()

async def main():
    task = asyncio.create_task(fetch('first', 0.02))
    results = await asyncio.gather(task, fetch('second', 0.01))
    print(results)

asyncio.run(main())
```

## Common Interview Questions
- **Q:** Does calling async function run it? **A:** No, it returns a coroutine.
- **Q:** What does `await` do? **A:** Suspends current coroutine until awaitable completes.
- **Q:** What is a Task? **A:** A coroutine scheduled on the event loop.
- **Q:** Is asyncio parallel? **A:** Not by itself; it is concurrent on one loop thread.
- **Q:** Why avoid blocking calls? **A:** They block every task on the loop.
- **Q:** What does `asyncio.run` do? **A:** Creates, runs, finalizes, and closes an event loop.
- **Q:** Gather order? **A:** Input order, not completion order.

## Pitfalls & Best Practices
- Do not forget to await coroutines.
- Use `asyncio.sleep`, not `time.sleep`, inside async code.
- Keep CPU-heavy work off the loop.
- Handle cancellation with cleanup.

## Related Topics
- asyncio in Depth
- concurrent.futures
- Choosing a Concurrency Model
