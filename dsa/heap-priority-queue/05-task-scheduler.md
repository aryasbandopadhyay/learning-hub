# 05. Task Scheduler

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given CPU tasks as capital letters and cooldown `n`, return the minimum time units needed to finish all tasks. Each unit either runs one task or idles; identical task letters must be separated by at least `n` units.

**Input**
- `tasks`: task identifiers.
- `n`: cooldown between equal tasks.

**Output**
- The minimum total number of time units, including idle time.

## Constraints
- `1 <= tasks.length <= 10^4`
- `tasks[i]` is an uppercase English letter.
- `0 <= n <= 100`

## Examples
```text
Input: tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
Explanation: One optimal schedule is `A B idle A B idle A B`, taking `8` time units.
```

## Understanding & Intuition
The most frequent tasks drive idle time. Simulation can pick the currently most frequent available task. A heap plus cooldown queue models that process, while the counting formula gives the optimal length directly.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate each interval by scanning all task counts for the best currently available task.
```python
from typing import List
from collections import Counter

class Solution:
    def leastInterval(self, tasks: List[str], n: int) -> int:
        counts = Counter(tasks)
        next_available = {task: 0 for task in counts}
        time = 0

        while counts:
            best = None
            for task, count in counts.items():
                if next_available[task] <= time and (best is None or count > counts[best]):
                    best = task

            if best is not None:
                counts[best] -= 1
                next_available[best] = time + n + 1
                if counts[best] == 0:
                    del counts[best]
                    del next_available[best]

            time += 1

        return time
```
- **Time:** O(T * u) — **Space:** O(u)

## Approach 2 — Better
**Idea:** Use a max-heap for available tasks and a queue for tasks cooling down.
```python
from typing import List
from collections import Counter, deque
import heapq

class Solution:
    def leastInterval(self, tasks: List[str], n: int) -> int:
        heap = [-count for count in Counter(tasks).values()]
        heapq.heapify(heap)
        cooldown = deque()  # (ready_time, remaining_negative_count)
        time = 0

        while heap or cooldown:
            time += 1

            if heap:
                count = heapq.heappop(heap) + 1  # Run one instance.
                if count < 0:
                    cooldown.append((time + n, count))

            if cooldown and cooldown[0][0] == time:
                heapq.heappush(heap, cooldown.popleft()[1])

        return time
```
- **Time:** O(T log u) — **Space:** O(u)

## Approach 3 — Optimal
**Idea:** Use the formula from arranging the most frequent tasks as frames, then fill gaps with other tasks.
```python
from typing import List
from collections import Counter

class Solution:
    def leastInterval(self, tasks: List[str], n: int) -> int:
        freq = list(Counter(tasks).values())
        max_freq = max(freq)
        max_count = sum(1 for count in freq if count == max_freq)

        # Frames: (max_freq - 1) groups of length n + 1, plus max_count final tasks.
        frame_length = (max_freq - 1) * (n + 1) + max_count
        return max(len(tasks), frame_length)
```
- **Time:** O(T) — **Space:** O(u)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(T * u) | O(u) |
| Better | O(T log u) | O(u) |
| Optimal | O(T) | O(u) |

## Edge Cases & Pitfalls
- If `n = 0`, the answer is just `len(tasks)`.
- Multiple task types can share the maximum frequency.
- In heap simulation, cooldown ready time should be after `n` separating intervals.

## Related
- Reorganize String
- Rearrange String k Distance Apart
- Minimum Number of Refueling Stops
