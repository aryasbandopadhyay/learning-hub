# 04. Synchronization Primitives

> Synchronization primitives coordinate access and progress among threads; interviewers use them to test race prevention and deadlock reasoning.

## Core Concepts
### Mutual exclusion
`Lock` guards a critical section. `RLock` allows the owning thread to acquire recursively and requires matching releases.

### Coordination
`Event` is a broadcast flag. `Condition` waits for a protected predicate. `Semaphore` limits concurrent access. `Barrier` waits for a fixed group.

### Deadlock avoidance
Use consistent lock ordering, short critical sections, timeouts when helpful, and higher-level queues for pipelines.

## How It Works Internally
Threading primitives are backed by lower-level OS synchronization via CPython thread APIs. Blocking waits release the GIL, letting other Python threads run. `Condition.wait()` atomically releases the associated lock and later reacquires it before returning, so the guarded predicate must be checked in a loop.

## Code Examples

```python
import threading
import time

items = []
condition = threading.Condition()

def producer():
    time.sleep(0.01)
    with condition:
        items.append('work')
        condition.notify()

threading.Thread(target=producer).start()
with condition:
    while not items:
        condition.wait()
    print(items.pop())
```

## Common Interview Questions
- **Q:** Lock vs RLock? **A:** RLock can be reacquired by its owning thread.
- **Q:** When use Semaphore? **A:** To cap access to a limited resource.
- **Q:** Event vs Condition? **A:** Event is a flag; Condition guards arbitrary state.
- **Q:** Why wait in loop? **A:** Wakeups may be stale or the predicate may be false.
- **Q:** What is Barrier? **A:** A rendezvous for N participants.
- **Q:** What causes deadlock? **A:** Cyclic waiting, often inconsistent lock order.
- **Q:** Does GIL remove locks? **A:** No, logical invariants still need protection.

## Pitfalls & Best Practices
- Use `with lock:` for exception-safe release.
- Avoid holding locks while calling unknown code.
- Define a global lock order.
- Prefer `Queue` for producer-consumer designs.

## Related Topics
- Threads in Python
- asyncio in Depth
- Choosing a Concurrency Model
