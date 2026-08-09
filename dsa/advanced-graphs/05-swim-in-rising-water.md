# 05. Swim in Rising Water

- **Difficulty:** Hard
- **Pattern:** Advanced Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
You are given an `n x n` grid where `grid[r][c]` is the time when cell `(r, c)` becomes passable. Start at `(0,0)` and reach `(n-1,n-1)`.

At time `t`, you may move in four directions only through cells with value at most `t`. Return the earliest time a path exists.

**Input**
- `grid`: an `n x n` matrix of unique elevation/time values.

**Output**
- An integer: the minimum possible maximum cell value along a start-to-finish path.

## Constraints
- `n == grid.length == grid[r].length`
- `1 <= n <= 50`
- `0 <= grid[r][c] < n^2`
- Every grid value is unique.

## Examples
```text
Input: grid = [[0,2],[1,3]]
Output: 3
Explanation: The destination has value 3, so no path can finish before time 3. At time 3, a complete path is available.
```

## Understanding & Intuition
A path's cost is the maximum elevation on it. Brute force can test every possible water level. Binary search improves this with repeated reachability checks, while Dijkstra directly minimizes the maximum cell value along the path.

## Approach 1 — Naive / Brute Force
**Idea:** Increase time from the start height until DFS can reach the target using cells with value at most that time.
```python
from typing import List

class Solution:
    def swimInWater(self, grid: List[List[int]]) -> int:
        n = len(grid)

        def can_reach(t: int) -> bool:
            if grid[0][0] > t:
                return False
            stack = [(0, 0)]
            seen = {(0, 0)}
            while stack:
                r, c = stack.pop()
                if (r, c) == (n - 1, n - 1):
                    return True
                for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < n and 0 <= nc < n and (nr, nc) not in seen and grid[nr][nc] <= t:
                        seen.add((nr, nc))
                        stack.append((nr, nc))
            return False

        for t in range(grid[0][0], n * n):
            if can_reach(t):
                return t
        return -1
```
- **Time:** O(N^4) — **Space:** O(N^2)

## Approach 2 — Better
**Idea:** Binary search the answer and run DFS/BFS as a feasibility check.
```python
from typing import List

class Solution:
    def swimInWater(self, grid: List[List[int]]) -> int:
        n = len(grid)

        def can_reach(t: int) -> bool:
            stack = [(0, 0)]
            seen = {(0, 0)}
            while stack:
                r, c = stack.pop()
                if (r, c) == (n - 1, n - 1):
                    return True
                for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < n and 0 <= nc < n and (nr, nc) not in seen and grid[nr][nc] <= t:
                        seen.add((nr, nc))
                        stack.append((nr, nc))
            return False

        lo, hi = max(grid[0][0], grid[n - 1][n - 1]), max(map(max, grid))
        while lo < hi:
            mid = (lo + hi) // 2
            if can_reach(mid):
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(N^2 log N) — **Space:** O(N^2)

## Approach 3 — Optimal
**Idea:** Dijkstra where the distance to a cell is the minimum possible maximum elevation seen so far.
```python
import heapq
from typing import List

class Solution:
    def swimInWater(self, grid: List[List[int]]) -> int:
        n = len(grid)
        heap = [(grid[0][0], 0, 0)]
        seen = {(0, 0)}

        while heap:
            time, r, c = heapq.heappop(heap)
            if (r, c) == (n - 1, n - 1):
                return time
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < n and 0 <= nc < n and (nr, nc) not in seen:
                    seen.add((nr, nc))
                    heapq.heappush(heap, (max(time, grid[nr][nc]), nr, nc))
        return -1
```
- **Time:** O(N^2 log N) — **Space:** O(N^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(N^4) | O(N^2) |
| Better | O(N^2 log N) | O(N^2) |
| Optimal | O(N^2 log N) | O(N^2) |

## Edge Cases & Pitfalls
- The answer is at least both endpoint heights.
- Mark a cell seen when pushing into the heap to avoid duplicates.
- This minimizes the maximum cell value, not the sum.

## Related
- Path With Minimum Effort
- Dijkstra on grids

