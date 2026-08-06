# 12. IPO

- **Difficulty:** Hard
- **Pattern:** greedy scheduling & assignment
- **Asked at:** Google, Amazon, Goldman Sachs

## Problem
Given `k`, initial capital `w`, and arrays `profits` and `capital`, project `i` can be done only if `w >= capital[i]`, then it adds `profits[i]` to `w`. Complete at most `k` projects and return maximum final capital.

## Examples
```text
Input: k = 2, w = 0, profits = [1,2,3], capital = [0,1,1]
Output: 4
Explanation: Do project 0, then project 2.
```

## Understanding & Intuition
At each step, available projects are those whose capital requirement is already met. Since profits are nonnegative, taking the largest available profit cannot reduce future options. A max-heap of available profits implements this choice.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try every affordable project for small inputs; use scan-greedy for large inputs.
```python
class Solution:
    def findMaximizedCapital(self, k: int, w: int, profits: list[int], capital: list[int]) -> int:
        n = len(profits)
        if n > 16 or k > 16:
            used = [False] * n
            for _ in range(k):
                best = -1
                for i in range(n):
                    if not used[i] and capital[i] <= w and (best == -1 or profits[i] > profits[best]):
                        best = i
                if best == -1:
                    break
                used[best] = True
                w += profits[best]
            return w
        from functools import lru_cache
        @lru_cache(None)
        def dfs(mask, cur, steps):
            if steps == k:
                return cur
            best = cur
            for i in range(n):
                if not (mask >> i) & 1 and capital[i] <= cur:
                    best = max(best, dfs(mask | (1 << i), cur + profits[i], steps + 1))
            return best
        return dfs(0, w, 0)
```
- **Time:** O(n!) — **Space:** O(2ⁿ)

## Approach 2 — Better
**Idea:** In each round, scan all unused affordable projects and choose the largest profit.
```python
class Solution:
    def findMaximizedCapital(self, k: int, w: int, profits: list[int], capital: list[int]) -> int:
        used = [False] * len(profits)
        for _ in range(k):
            best = -1
            for i in range(len(profits)):
                if not used[i] and capital[i] <= w and (best == -1 or profits[i] > profits[best]):
                    best = i
            if best == -1:
                break
            used[best] = True
            w += profits[best]
        return w
```
- **Time:** O(kn) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by capital, push newly affordable profits into a max-heap, and choose the largest profit up to `k` times.
```python
class Solution:
    def findMaximizedCapital(self, k: int, w: int, profits: list[int], capital: list[int]) -> int:
        import heapq
        projects = sorted(zip(capital, profits))
        heap = []
        i = 0
        for _ in range(k):
            while i < len(projects) and projects[i][0] <= w:
                heapq.heappush(heap, -projects[i][1])
                i += 1
            if not heap:
                break
            w += -heapq.heappop(heap)
        return w
```
- **Time:** O((n + k) log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n!) | O(2ⁿ) |
| Better | O(kn) | O(n) |
| Optimal | O((n + k) log n) | O(n) |

## Edge Cases & Pitfalls
- Stop early when no project is affordable.
- Do not add projects to the heap before their requirement is met.
- Zero-profit projects are harmless but may be unnecessary.

## Related
- Minimum Number of Refueling Stops
- Course Schedule III
