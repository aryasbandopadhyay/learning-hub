# 05. Multiprocessing

> Multiprocessing uses separate OS processes for parallelism and isolation, bypassing the GIL at the cost of serialization and process overhead.

## Core Concepts
### Process model
Each process has its own interpreter, GIL, heap, and address space. Sharing requires IPC or shared memory.

### spawn vs fork
Windows uses `spawn`, starting a fresh interpreter that imports the main module. Unix may use `fork`, which copies process state. Portable code needs the `__main__` guard.

### IPC and pools
Queues, pipes, shared memory, managers, `Pool`, and process executors distribute work and results, usually through pickling.

## How It Works Internally
Under `spawn`, the child imports the parent module, unpickles the target and arguments, runs the callable, and serializes results back. `multiprocessing.Queue` uses pickle and pipe-like transport. Managers expose proxy objects through a server process; shared memory avoids large byte copies but requires explicit synchronization and lifecycle cleanup.

## Code Examples

```python
from multiprocessing import Process, Queue

def worker(q, out):
    value = q.get()
    out.put(value * value)

def main():
    q, out = Queue(), Queue()
    p = Process(target=worker, args=(q, out))
    p.start()
    q.put(7)
    print(out.get(timeout=2))
    p.join(timeout=2)

if __name__ == '__main__':
    main()
```

## Common Interview Questions
- **Q:** Why bypass GIL? **A:** Each process has its own interpreter and GIL.
- **Q:** Why `__main__` guard? **A:** Spawn imports the main module; unguarded creation can recurse.
- **Q:** What must be picklable? **A:** Targets, arguments, and results sent to workers.
- **Q:** Queue vs Pipe? **A:** Queue is higher-level and multi-producer friendly.
- **Q:** Manager downside? **A:** Proxy calls add server-process overhead.
- **Q:** Pool use case? **A:** Many independent CPU-heavy tasks.
- **Q:** Main cost? **A:** Startup, memory, IPC, and serialization.

## Pitfalls & Best Practices
- Always guard process creation portably.
- Batch tiny tasks to amortize IPC.
- Keep worker functions importable.
- Close and join pools/processes cleanly.

## Related Topics
- The GIL — Global Interpreter Lock
- concurrent.futures
- Choosing a Concurrency Model
