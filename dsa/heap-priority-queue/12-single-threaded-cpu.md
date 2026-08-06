# 12. Single-Threaded CPU

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
You are given `tasks`, where `tasks[i] = [enqueueTime, processingTime]`. A single-threaded CPU chooses among available tasks the one with the shortest processing time, breaking ties by original index. Return the order of processed task indices. Constraints: `1 <= len(tasks) <= 10^5`, `1 <= enqueueTime, processingTime <= 10^9`.

## Examples
```text
Input: tasks = [[1,2],[2,4],[3,2],[4,1]]
Output: [0,2,3,1]
Explanation: The CPU runs task 0, then shortest available task 2, then 3, then 1.
```

## Understanding & Intuition
Tasks become available over time, then priority is processing time and index. Scanning all tasks at each step is too slow. Sorting by enqueue time plus a heap of available tasks models the scheduler efficiently.

## Approach 1 — Naive / Brute Force
**Idea:** At each step, scan all unfinished tasks to find available tasks and choose the best one.
```python
from typing import List

class Solution:
    def getOrder(self, tasks: List[List[int]]) -> List[int]:
        n = len(tasks)
        done = [False] * n
        result = []
        time = min(task[0] for task in tasks)

        while len(result) < n:
            best = -1
            for i, (enqueue, process) in enumerate(tasks):
                if not done[i] and enqueue <= time:
                    if best == -1 or (process, i) < (tasks[best][1], best):
                        best = i

            if best == -1:
                time = min(tasks[i][0] for i in range(n) if not done[i])
                continue

            done[best] = True
            result.append(best)
            time += tasks[best][1]

        return result
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort tasks by enqueue time and repeatedly sort the available list before choosing.
```python
from typing import List

class Solution:
    def getOrder(self, tasks: List[List[int]]) -> List[int]:
        indexed = sorted((enqueue, process, i) for i, (enqueue, process) in enumerate(tasks))
        available = []
        result = []
        time = 0
        i = 0

        while i < len(indexed) or available:
            if not available and time < indexed[i][0]:
                time = indexed[i][0]

            while i < len(indexed) and indexed[i][0] <= time:
                enqueue, process, original = indexed[i]
                available.append((process, original))
                i += 1

            available.sort(reverse=True)
            process, original = available.pop()
            result.append(original)
            time += process

        return result
```
- **Time:** O(n^2 log n) worst — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by enqueue time and use a min-heap keyed by `(processingTime, index)` for available tasks.
```python
from typing import List
import heapq

class Solution:
    def getOrder(self, tasks: List[List[int]]) -> List[int]:
        indexed = sorted((enqueue, process, i) for i, (enqueue, process) in enumerate(tasks))
        heap = []
        result = []
        time = 0
        i = 0
        n = len(tasks)

        while i < n or heap:
            if not heap and time < indexed[i][0]:
                time = indexed[i][0]

            while i < n and indexed[i][0] <= time:
                enqueue, process, original = indexed[i]
                heapq.heappush(heap, (process, original))
                i += 1

            process, original = heapq.heappop(heap)
            result.append(original)
            time += process

        return result
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2 log n) worst | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- If the heap is empty, jump time to the next enqueue time.
- Tie-break by original index after processing time.
- Enqueue and processing times can be large; avoid interval-by-interval simulation.

## Related
- Task Scheduler
- Meeting Rooms II
- Process Tasks Using Servers
