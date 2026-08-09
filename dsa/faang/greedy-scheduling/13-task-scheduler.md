# 13. Task Scheduler

- **Difficulty:** Medium
- **Pattern:** greedy scheduling
- **Asked at:** Amazon, Facebook, Google

## Problem
Implement `leastInterval` for **Task Scheduler**. Given a list of CPU `tasks`, where each task is a single uppercase letter, and a nonnegative cooldown `n`, return the least number of time units needed to finish all tasks. The same task letter must be separated by at least `n` time units, while different tasks may run in consecutive units. The CPU may be idle if no valid task can be scheduled.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `tasks`: list; CPU tasks.
- `n`: integer; problem size or count as defined above.

**Output**
- A single integer.

## Constraints
- `1 <= len(tasks) <= 10^4`, `0 <= n <= 100`, and every task is an uppercase English letter

## Examples
```text
Input: tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
Explanation: One optimal schedule is A, B, idle, A, B, idle, A, B. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
The bottleneck is the most frequent task, because copies of it force cooldown gaps. Other tasks can fill those gaps and reduce idles. If the gaps cannot all be filled, idle time is unavoidable; otherwise the answer is just the number of tasks.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate one time unit at a time, always choosing the remaining available task with the largest count.
```python
class Solution:
    def leastInterval(self, tasks: list[str], n: int) -> int:
        counts = {}
        for task in tasks:
            counts[task] = counts.get(task, 0) + 1
        cooldown_until = {task: 0 for task in counts}
        time = 0
        remaining = len(tasks)
        while remaining:
            best = None
            for task, count in counts.items():
                if count > 0 and cooldown_until[task] <= time:
                    if best is None or count > counts[best] or (count == counts[best] and task < best):
                        best = task
            if best is not None:
                counts[best] -= 1
                cooldown_until[best] = time + n + 1
                remaining -= 1
            time += 1
        return time
```
- **Time:** O(TK) — **Space:** O(K)

## Approach 2 — Better
**Idea:** Use a max-heap for available tasks and a queue for tasks cooling down.
```python
class Solution:
    def leastInterval(self, tasks: list[str], n: int) -> int:
        from collections import Counter, deque
        import heapq

        heap = [(-count, task) for task, count in Counter(tasks).items()]
        heapq.heapify(heap)
        cooling = deque()
        time = 0
        while heap or cooling:
            while cooling and cooling[0][0] <= time:
                ready_time, count, task = cooling.popleft()
                heapq.heappush(heap, (count, task))
            if heap:
                count, task = heapq.heappop(heap)
                count += 1
                if count < 0:
                    cooling.append((time + n + 1, count, task))
            time += 1
        return time
```
- **Time:** O(T log K) — **Space:** O(K)

## Approach 3 — Optimal
**Idea:** Count the most frequent task and compute how many cooldown slots it creates.
```python
class Solution:
    def leastInterval(self, tasks: list[str], n: int) -> int:
        from collections import Counter

        freq = Counter(tasks).values()
        max_freq = max(freq)
        max_count = sum(1 for count in freq if count == max_freq)
        frame = (max_freq - 1) * (n + 1) + max_count
        return max(len(tasks), frame)
```
- **Time:** O(T + K) — **Space:** O(K)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(TK) | O(K) |
| Better | O(T log K) | O(K) |
| Optimal | O(T + K) | O(K) |

## Edge Cases & Pitfalls
- When `n = 0`, no idle time is needed.
- Multiple task types can tie for maximum frequency.
- The formula must be capped below by `len(tasks)` because filled schedules have no idles.

## Related
- Reorganize String
- Distant Barcodes
