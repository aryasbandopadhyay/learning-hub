# 12. Max Value of Equation

- **Difficulty:** Hard
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given `points` sorted by increasing x-coordinate, where `points[i] = [xi, yi]`, and an integer `k`, return the maximum value of `yi + yj + abs(xi - xj)` over pairs `i < j` with `xj - xi <= k`.

Constraints: `2 <= len(points) <= 100000`; `-100000000 <= xi, yi <= 100000000`; x-values are strictly increasing; `1 <= k <= 200000000`.

## Examples
```text
Input: points = [[1, 3], [2, 0], [5, 10], [6, -10]], k = 1
Output: 4
Explanation: The pair `[1,3]` and `[2,0]` gives `3 + 0 + 1 = 4`.
```

## Understanding & Intuition
For `i < j`, the expression becomes `(yi - xi) + (yj + xj)`. As `j` moves right, only prior points within distance `k` are eligible. We need the maximum `yi - xi` in that sliding x-range.

## Approach 1 — Naive / Brute Force
**Idea:** Check every pair and evaluate the equation when the distance constraint holds.
```python
class Solution:
    def findMaxValueOfEquation(self, points: list[list[int]], k: int) -> int:
        best = -10**30
        n = len(points)
        for i in range(n):
            for j in range(i + 1, n):
                if points[j][0] - points[i][0] <= k:
                    val = points[i][1] + points[j][1] + points[j][0] - points[i][0]
                    best = max(best, val)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Maintain eligible previous points in a max-heap ordered by `yi - xi`.
```python
class Solution:
    def findMaxValueOfEquation(self, points: list[list[int]], k: int) -> int:
        from heapq import heappush, heappop
        heap = []
        best = -10**30
        for x, y in points:
            while heap and x - heap[0][1] > k:
                heappop(heap)
            if heap:
                best = max(best, x + y - heap[0][0])
            heappush(heap, (x - y, x))
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a decreasing deque of candidate `(yi - xi, xi)` values inside the distance window.
```python
class Solution:
    def findMaxValueOfEquation(self, points: list[list[int]], k: int) -> int:
        q = []
        head = 0
        best = -10**30
        for x, y in points:
            while head < len(q) and x - q[head][1] > k:
                head += 1
            if head < len(q):
                best = max(best, x + y + q[head][0])
            cur = y - x
            while head < len(q) and q[-1][0] <= cur:
                q.pop()
            q.append((cur, x))
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The input is sorted, so `abs(xi - xj)` simplifies to `xj - xi` for `i < j`.
- Remove candidates outside the x-distance limit before evaluating the current point.

## Related
- Sliding Window Maximum
- Constrained Subsequence Sum
