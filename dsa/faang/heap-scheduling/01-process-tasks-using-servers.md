# 01. Process Tasks Using Servers

- **Difficulty:** Medium
- **Pattern:** heaps for scheduling/simulation
- **Asked at:** Google, Amazon, Meta

## Problem
You are given `servers`, where `servers[i]` is the weight of server `i`, and `tasks`, where `tasks[j]` is the processing time of task `j`. Task `j` becomes available at time `j`. Assign each task to the available server with the smallest weight, breaking ties by smaller index. If no server is available, wait until the next server finishes and apply the same tie-break among all servers that are then free.

Return the list of assigned server indices.

**Input**
- `servers`: a `list[int]`; server weights by server index.
- `tasks`: a `list[int]`; the task data, as described above.

**Output**
- A `list[int]`. Return the list of assigned server indices. This judge compares the sequence exactly: `answer[j]` must be the server assigned to task `j`, in increasing task index order.

## Constraints
- `1 <= len(servers), len(tasks) <= 2 * 10^5`, `1 <= servers[i], tasks[j] <= 2 * 10^5`.

## Examples
```text
Input: servers = [3,3,2], tasks = [1,2,3,2,1,2]
Output: [2,2,0,2,1,2]
Explanation: The lightest available server is always selected; when all are busy, time jumps to the next completion. The output is written in the required deterministic order.
```

## Understanding & Intuition
Tasks arrive one per time unit, while servers finish at irregular times. The key is to keep free servers ordered by priority and busy servers ordered by finishing time. If the free set is empty, the simulation clock jumps rather than advancing one unit at a time.

## Approach 1 — Naive / Brute Force
**Idea:** Keep free and busy lists, scan them whenever a task arrives, and linearly choose the best free server.
```python
class Solution:
    def assignTasks(self, servers: list[int], tasks: list[int]) -> list[int]:
        time = 0
        free = list(range(len(servers)))
        busy = []
        ans = []
        for j, duration in enumerate(tasks):
            time = max(time, j)
            changed = True
            while changed:
                changed = False
                remaining = []
                for finish, idx in busy:
                    if finish <= time:
                        free.append(idx)
                        changed = True
                    else:
                        remaining.append((finish, idx))
                busy = remaining
            if not free:
                time = min(finish for finish, _ in busy)
                remaining = []
                for finish, idx in busy:
                    if finish <= time:
                        free.append(idx)
                    else:
                        remaining.append((finish, idx))
                busy = remaining
            best = min(free, key=lambda i: (servers[i], i))
            free.remove(best)
            ans.append(best)
            busy.append((time + duration, best))
        return ans
```
- **Time:** O(m(n + m)) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a heap for busy servers, but still scan the free list to choose the best server.
```python
class Solution:
    def assignTasks(self, servers: list[int], tasks: list[int]) -> list[int]:
        import heapq
        time = 0
        free = list(range(len(servers)))
        busy = []
        ans = []
        for j, duration in enumerate(tasks):
            time = max(time, j)
            while busy and busy[0][0] <= time:
                _, idx = heapq.heappop(busy)
                free.append(idx)
            if not free:
                time = busy[0][0]
                while busy and busy[0][0] <= time:
                    _, idx = heapq.heappop(busy)
                    free.append(idx)
            best_pos = min(range(len(free)), key=lambda p: (servers[free[p]], free[p]))
            idx = free.pop(best_pos)
            ans.append(idx)
            heapq.heappush(busy, (time + duration, idx))
        return ans
```
- **Time:** O(mn + m log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain one heap of free servers by `(weight, index)` and one heap of busy servers by `(finish_time, weight, index)`.
```python
class Solution:
    def assignTasks(self, servers: list[int], tasks: list[int]) -> list[int]:
        import heapq
        free = [(w, i) for i, w in enumerate(servers)]
        heapq.heapify(free)
        busy = []
        time = 0
        ans = []
        for j, duration in enumerate(tasks):
            time = max(time, j)
            while busy and busy[0][0] <= time:
                _, w, i = heapq.heappop(busy)
                heapq.heappush(free, (w, i))
            if not free:
                time = busy[0][0]
                while busy and busy[0][0] <= time:
                    _, w, i = heapq.heappop(busy)
                    heapq.heappush(free, (w, i))
            w, i = heapq.heappop(free)
            ans.append(i)
            heapq.heappush(busy, (time + duration, w, i))
        return ans
```
- **Time:** O((n + m) log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m(n + m)) | O(n) |
| Better | O(mn + m log n) | O(n) |
| Optimal | O((n + m) log n) | O(n) |

## Edge Cases & Pitfalls
- Jump time forward when all servers are busy.
- Release every server that finishes at the jumped time before assigning.
- Tie-break by server weight, then index.

## Related
- Single-Threaded CPU
- Meeting Rooms III
