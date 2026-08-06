# 10. Task Scheduler

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given a list of CPU tasks represented by capital letters and a non-negative cooldown `n`, return the least number of time intervals needed to finish all tasks. The same task type must be separated by at least `n` intervals. Constraints: `1 <= len(tasks) <= 10^4`, `0 <= n <= 100`.

## Examples
```text
Input: tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
Explanation: One optimal schedule is A -> B -> idle -> A -> B -> idle -> A -> B.
```

## Understanding & Intuition
The most frequent tasks create the skeleton of the schedule. If a task appears `max_freq` times, it creates `max_freq - 1` gaps of length `n`. Other tasks fill those gaps; if they overflow, the answer is just the number of tasks.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate time and choose any currently available task with the largest remaining count.
```python
from collections import Counter
from typing import List

class Solution:
    def leastInterval(self, tasks: List[str], n: int) -> int:
        remaining = Counter(tasks)
        cooldown = {}
        time = 0

        while remaining:
            available = [
                task for task in remaining
                if cooldown.get(task, -1) <= time
            ]
            if available:
                task = max(available, key=lambda t: remaining[t])
                remaining[task] -= 1
                if remaining[task] == 0:
                    del remaining[task]
                cooldown[task] = time + n + 1
            time += 1
        return time
```
- **Time:** O(T * A) — **Space:** O(A)

## Approach 2 — Better
**Idea:** Use a max-heap and cooldown queue to simulate valid task choices efficiently.
```python
from collections import Counter, deque
from heapq import heappop, heappush
from typing import List

class Solution:
    def leastInterval(self, tasks: List[str], n: int) -> int:
        heap = [-count for count in Counter(tasks).values()]
        import heapq
        heapq.heapify(heap)
        wait = deque()
        time = 0

        while heap or wait:
            time += 1
            if heap:
                count = heappop(heap) + 1
                if count:
                    wait.append((time + n, count))
            if wait and wait[0][0] == time:
                heappush(heap, wait.popleft()[1])
        return time
```
- **Time:** O(T log A) — **Space:** O(A)

## Approach 3 — Optimal
**Idea:** Compute the idle slots forced by the most frequent tasks.
```python
from collections import Counter
from typing import List

class Solution:
    def leastInterval(self, tasks: List[str], n: int) -> int:
        counts = Counter(tasks).values()
        max_freq = max(counts)
        num_max = sum(1 for count in Counter(tasks).values() if count == max_freq)
        frame = (max_freq - 1) * (n + 1) + num_max
        return max(len(tasks), frame)
```
- **Time:** O(T) — **Space:** O(A)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(T * A) | O(A) |
| Better | O(T log A) | O(A) |
| Optimal | O(T) | O(A) |

## Edge Cases & Pitfalls
- If `n == 0`, return `len(tasks)`.
- Multiple task types may tie for maximum frequency.
- The answer is never less than the number of tasks.

## Related
- Reorganize String
- Rearrange Barcodes
