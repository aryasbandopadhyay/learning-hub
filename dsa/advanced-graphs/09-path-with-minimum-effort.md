# 09. Path With Minimum Effort

- **Difficulty:** Medium
- **Pattern:** Advanced Graphs
- **Asked at:** Google, Amazon, Microsoft, Bloomberg

## Problem
You are given an `m x n` grid of heights. A path goes from top-left to bottom-right using four-directional moves.

The effort of a path is the largest absolute height difference between adjacent cells on it. Return the minimum possible effort.

**Input**
- `heights`: a 2-D list of integer heights.

**Output**
- An integer: the minimum achievable maximum adjacent difference.

## Constraints
- `m == heights.length`
- `n == heights[r].length`
- `1 <= m, n <= 100`
- `1 <= heights[r][c] <= 10^6`.

## Examples
```text
Input: heights = [[1,2,2],[3,8,2],[5,3,5]]
Output: 2
Explanation: A route can avoid the large jump through height 8. Along the best route, the largest adjacent difference is 2.
```

## Understanding & Intuition
The cost of a route is its largest edge, not its sum. Brute force can enumerate all paths. Binary search checks if a path exists using only edges up to a candidate effort, while Dijkstra directly minimizes the maximum edge seen so far.

## Approach 1 — Naive / Brute Force
**Idea:** DFS all simple paths and minimize the maximum edge difference.
```python
from typing import List

class Solution:
    def minimumEffortPath(self, heights: List[List[int]]) -> int:
        rows, cols = len(heights), len(heights[0])
        best = float("inf")

        def dfs(r: int, c: int, effort: int, seen: set[tuple[int, int]]) -> None:
            nonlocal best
            if effort >= best:
                return
            if (r, c) == (rows - 1, cols - 1):
                best = effort
                return
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols and (nr, nc) not in seen:
                    step = abs(heights[r][c] - heights[nr][nc])
                    seen.add((nr, nc))
                    dfs(nr, nc, max(effort, step), seen)
                    seen.remove((nr, nc))

        dfs(0, 0, 0, {(0, 0)})
        return best
```
- **Time:** O((RC)!) — **Space:** O(RC)

## Approach 2 — Better
**Idea:** Binary search the effort and use DFS to test whether the target is reachable.
```python
from typing import List

class Solution:
    def minimumEffortPath(self, heights: List[List[int]]) -> int:
        rows, cols = len(heights), len(heights[0])

        def can(limit: int) -> bool:
            stack = [(0, 0)]
            seen = {(0, 0)}
            while stack:
                r, c = stack.pop()
                if (r, c) == (rows - 1, cols - 1):
                    return True
                for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < rows and 0 <= nc < cols and (nr, nc) not in seen:
                        if abs(heights[r][c] - heights[nr][nc]) <= limit:
                            seen.add((nr, nc))
                            stack.append((nr, nc))
            return False

        lo, hi = 0, max(max(row) for row in heights) - min(min(row) for row in heights)
        while lo < hi:
            mid = (lo + hi) // 2
            if can(mid):
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(RC log W) — **Space:** O(RC)

## Approach 3 — Optimal
**Idea:** Dijkstra where distance is the minimum possible maximum edge difference to each cell.
```python
import heapq
from typing import List

class Solution:
    def minimumEffortPath(self, heights: List[List[int]]) -> int:
        rows, cols = len(heights), len(heights[0])
        effort = [[float("inf")] * cols for _ in range(rows)]
        effort[0][0] = 0
        heap = [(0, 0, 0)]

        while heap:
            cur, r, c = heapq.heappop(heap)
            if (r, c) == (rows - 1, cols - 1):
                return cur
            if cur != effort[r][c]:
                continue
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols:
                    step = abs(heights[r][c] - heights[nr][nc])
                    new_effort = max(cur, step)
                    if new_effort < effort[nr][nc]:
                        effort[nr][nc] = new_effort
                        heapq.heappush(heap, (new_effort, nr, nc))
        return 0
```
- **Time:** O(RC log(RC)) — **Space:** O(RC)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((RC)!) | O(RC) |
| Better | O(RC log W) | O(RC) |
| Optimal | O(RC log(RC)) | O(RC) |

## Edge Cases & Pitfalls
- A `1 x 1` grid has effort `0`.
- The path cost is the maximum edge, not the sum.
- Binary search upper bound can be the global height range.

## Related
- Swim in Rising Water
- Dijkstra on grids

