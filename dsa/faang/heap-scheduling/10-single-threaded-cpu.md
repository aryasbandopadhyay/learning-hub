# 10. Single Threaded CPU

- **Difficulty:** Medium
- **Pattern:** Heap / Scheduling
- **Asked at:** Amazon, Google, Meta

## Problem
You are given `tasks`, where `tasks[i] = [enqueueTime, processingTime]` describes the time task `i` becomes available and how long it takes a single-threaded CPU to finish it. The CPU is idle initially. Whenever the CPU is idle, it chooses the available task with the shortest processing time; if there is a tie, it chooses the smaller original index. Return the order of task indices processed by the CPU.

Constraints: `1 <= len(tasks) <= 10^5`, `1 <= enqueueTime, processingTime <= 10^9`.

## Examples
```text
Input: tasks = [[1,2],[2,4],[3,2],[4,1]]
Output: [0,2,3,1]
Explanation: Task 0 runs first, then among available tasks the CPU repeatedly picks the shortest processing time with index as tie-breaker.
```

## Understanding & Intuition
The CPU only needs to reconsider choices when a task finishes or when it jumps from idle time to the next enqueue time. Available tasks are ordered by `(processingTime, index)`. Preserving original indices is essential because sorting by enqueue time loses the original positions.

## Approach 1 — Naive / Brute Force
**Idea:** Keep all unfinished tasks in a list. At each CPU decision, scan the list to find all currently available tasks and choose the one with minimum `(processingTime, index)`; if none is available, jump time to the next enqueue time.
```python
class Solution:
    def getOrder(self, tasks):
        remaining = [(task[0], task[1], i) for i, task in enumerate(tasks)]
        time = 0
        order = []
        while remaining:
            best_pos = -1
            best_key = None
            next_time = min(t[0] for t in remaining)
            if time < next_time:
                time = next_time
            for pos, (enqueue, process, idx) in enumerate(remaining):
                if enqueue <= time:
                    key = (process, idx)
                    if best_key is None or key < best_key:
                        best_key = key
                        best_pos = pos
            enqueue, process, idx = remaining.pop(best_pos)
            time += process
            order.append(idx)
        return order
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort tasks by enqueue time once, then maintain a sorted ready list of `(processingTime, index)` pairs. Inserting into the sorted list is still linear, but we avoid repeatedly scanning tasks that have not arrived.
```python
class Solution:
    def getOrder(self, tasks):
        from bisect import insort
        indexed = sorted((task[0], task[1], i) for i, task in enumerate(tasks))
        n = len(indexed)
        i = 0
        time = 0
        ready = []
        order = []
        while i < n or ready:
            if not ready and time < indexed[i][0]:
                time = indexed[i][0]
            while i < n and indexed[i][0] <= time:
                enqueue, process, idx = indexed[i]
                insort(ready, (process, idx))
                i += 1
            process, idx = ready.pop(0)
            time += process
            order.append(idx)
        return order
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort tasks by enqueue time and push available tasks into a min-heap keyed by `(processingTime, index)`. Each task is pushed and popped once.
```python
class Solution:
    def getOrder(self, tasks):
        import heapq
        indexed = sorted((task[0], task[1], i) for i, task in enumerate(tasks))
        n = len(indexed)
        i = 0
        time = 0
        heap = []
        order = []
        while i < n or heap:
            if not heap and time < indexed[i][0]:
                time = indexed[i][0]
            while i < n and indexed[i][0] <= time:
                enqueue, process, idx = indexed[i]
                heapq.heappush(heap, (process, idx))
                i += 1
            process, idx = heapq.heappop(heap)
            time += process
            order.append(idx)
        return order
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- If the heap or ready list is empty, jump time to the next task's enqueue time.
- Always tie-break by original task index, not sorted position.
- Use integer time because total processing can exceed 32-bit ranges.

## Related
- Task Scheduler
- Meeting Rooms II
- Process Tasks Using Servers
