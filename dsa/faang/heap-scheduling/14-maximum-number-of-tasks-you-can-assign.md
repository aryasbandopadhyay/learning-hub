# 14. Maximum Number of Tasks You Can Assign

- **Difficulty:** Hard
- **Pattern:** Binary Search / Greedy / Heap
- **Asked at:** Google, Amazon, Meta

## Problem
You are given arrays `tasks` and `workers`, where `tasks[i]` is the strength required by task `i` and `workers[j]` is the strength of worker `j`. Each worker can do at most one task. You also have `pills` pills; each pill can be given to one worker to increase that worker's strength by `strength`. Return the maximum number of tasks that can be assigned.

Constraints: `1 <= len(tasks), len(workers) <= 5 * 10^4`, `0 <= pills <= len(workers)`, `0 <= strength <= 10^9`, `1 <= tasks[i], workers[j] <= 10^9`.

## Examples
```text
Input: tasks = [3,2,1], workers = [0,3,3], pills = 1, strength = 1
Output: 3
Explanation: Assign the worker with strength 0 to task 1 using a pill, and the two workers with strength 3 to tasks 2 and 3.
```

## Understanding & Intuition
If we can assign `k` tasks, we should use the `k` easiest tasks and the `k` strongest workers. That monotonic property allows binary search on `k`. The check must spend pills only when a worker cannot otherwise cover a selected task.

## Approach 1 — Naive / Brute Force
**Idea:** Sort both arrays and test every possible answer from small to large. For each `k`, greedily assign the `k` easiest tasks from hardest to easiest using a sorted worker list.
```python
class Solution:
    def maxTaskAssign(self, tasks, workers, pills, strength):
        from bisect import bisect_left
        sorted_tasks = sorted(tasks)
        sorted_workers = sorted(workers)

        def can(k):
            available = sorted_workers[-k:] if k else []
            pills_left = pills
            for task in reversed(sorted_tasks[:k]):
                pos = bisect_left(available, task)
                if pos < len(available):
                    available.pop(pos)
                    continue
                if pills_left == 0:
                    return False
                pos = bisect_left(available, task - strength)
                if pos == len(available):
                    return False
                available.pop(pos)
                pills_left -= 1
            return True

        answer = 0
        for k in range(1, min(len(tasks), len(workers)) + 1):
            if can(k):
                answer = k
            else:
                break
        return answer
```
- **Time:** O(q^3) — **Space:** O(q)

## Approach 2 — Better
**Idea:** Keep the same sorted-list feasibility check, but use binary search because feasibility is monotonic.
```python
class Solution:
    def maxTaskAssign(self, tasks, workers, pills, strength):
        from bisect import bisect_left
        sorted_tasks = sorted(tasks)
        sorted_workers = sorted(workers)

        def can(k):
            available = sorted_workers[-k:] if k else []
            pills_left = pills
            for task in reversed(sorted_tasks[:k]):
                pos = bisect_left(available, task)
                if pos < len(available):
                    available.pop(pos)
                    continue
                if pills_left == 0:
                    return False
                pos = bisect_left(available, task - strength)
                if pos == len(available):
                    return False
                available.pop(pos)
                pills_left -= 1
            return True

        low, high = 0, min(len(tasks), len(workers))
        while low < high:
            mid = (low + high + 1) // 2
            if can(mid):
                low = mid
            else:
                high = mid - 1
        return low
```
- **Time:** O(q^2 log q) — **Space:** O(q)

## Approach 3 — Optimal
**Idea:** Binary search the number of tasks. To check `k`, scan the `k` strongest workers from weakest to strongest, maintain tasks they could do with a pill in a deque, and greedily use no pill for the easiest doable task or a pill for the hardest currently reachable task.
```python
class Solution:
    def maxTaskAssign(self, tasks, workers, pills, strength):
        from collections import deque
        sorted_tasks = sorted(tasks)
        sorted_workers = sorted(workers)

        def can(k):
            queue = deque()
            task_index = 0
            pills_left = pills
            selected_workers = sorted_workers[-k:] if k else []
            for worker in selected_workers:
                while task_index < k and sorted_tasks[task_index] <= worker + strength:
                    queue.append(sorted_tasks[task_index])
                    task_index += 1
                if not queue:
                    return False
                if queue[0] <= worker:
                    queue.popleft()
                else:
                    if pills_left == 0:
                        return False
                    queue.pop()
                    pills_left -= 1
            return True

        low, high = 0, min(len(tasks), len(workers))
        while low < high:
            mid = (low + high + 1) // 2
            if can(mid):
                low = mid
            else:
                high = mid - 1
        return low
```
- **Time:** O((n + m) log(n + m)) — **Space:** O(q)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(q^3) | O(q) |
| Better | O(q^2 log q) | O(q) |
| Optimal | O((n + m) log(n + m)) | O(q) |

## Edge Cases & Pitfalls
- Use the easiest `k` tasks and strongest `k` workers when testing a candidate answer.
- A pill should be consumed only when the worker cannot perform the chosen task unaided.
- `q = min(len(tasks), len(workers))`.

## Related
- Heaters
- Boats to Save People
- Maximum Performance of a Team
