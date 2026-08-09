# 10. Minimum Cost to Make at Least One Valid Path in a Grid

- **Difficulty:** Hard
- **Pattern:** Advanced Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
You are given an `m x n` grid of arrows: `1` right, `2` left, `3` down, and `4` up.

Starting at the top-left, following arrows should lead to the bottom-right. You may change any cell's arrow for cost `1`. Return the minimum cost to make at least one valid path.

**Input**
- `grid`: a 2-D list of direction values from `1` to `4`.

**Output**
- An integer: the minimum number of arrow changes required.

## Constraints
- `m == grid.length`
- `n == grid[r].length`
- `1 <= m, n <= 100`
- `grid[r][c]` is one of `1`, `2`, `3`, or `4`.

## Examples
```text
Input: grid = [[1,1,1,1],[2,2,2,2],[1,1,1,1],[2,2,2,2]]
Output: 3
Explanation: Changing three arrows creates a continuous route from the top-left to the bottom-right. Fewer changes cannot connect a valid directed path.
```

## Understanding & Intuition
Each move has cost `0` if it follows the current arrow and `1` otherwise. DFS can enumerate possible simple paths. Dijkstra works, but because all edge weights are only `0` or `1`, 0-1 BFS with a deque is the optimal specialized algorithm.

## Approach 1 — Naive / Brute Force
**Idea:** DFS every simple path and accumulate direction-change costs.
```python
from typing import List

class Solution:
    def minCost(self, grid: List[List[int]]) -> int:
        rows, cols = len(grid), len(grid[0])
        dirs = [(0, 1), (0, -1), (1, 0), (-1, 0)]
        best = float("inf")

        def dfs(r: int, c: int, cost: int, seen: set[tuple[int, int]]) -> None:
            nonlocal best
            if cost >= best:
                return
            if (r, c) == (rows - 1, cols - 1):
                best = cost
                return
            for idx, (dr, dc) in enumerate(dirs, start=1):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols and (nr, nc) not in seen:
                    seen.add((nr, nc))
                    dfs(nr, nc, cost + (0 if grid[r][c] == idx else 1), seen)
                    seen.remove((nr, nc))

        dfs(0, 0, 0, {(0, 0)})
        return best
```
- **Time:** O((MN)!) — **Space:** O(MN)

## Approach 2 — Better
**Idea:** Run standard Dijkstra on the implicit grid graph.
```python
import heapq
from typing import List

class Solution:
    def minCost(self, grid: List[List[int]]) -> int:
        rows, cols = len(grid), len(grid[0])
        dirs = [(0, 1), (0, -1), (1, 0), (-1, 0)]
        dist = [[float("inf")] * cols for _ in range(rows)]
        dist[0][0] = 0
        heap = [(0, 0, 0)]

        while heap:
            cost, r, c = heapq.heappop(heap)
            if (r, c) == (rows - 1, cols - 1):
                return cost
            if cost != dist[r][c]:
                continue
            for idx, (dr, dc) in enumerate(dirs, start=1):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols:
                    new_cost = cost + (0 if grid[r][c] == idx else 1)
                    if new_cost < dist[nr][nc]:
                        dist[nr][nc] = new_cost
                        heapq.heappush(heap, (new_cost, nr, nc))
        return -1
```
- **Time:** O(MN log(MN)) — **Space:** O(MN)

## Approach 3 — Optimal
**Idea:** Use 0-1 BFS: push zero-cost moves to the front and one-cost moves to the back.
```python
from collections import deque
from typing import List

class Solution:
    def minCost(self, grid: List[List[int]]) -> int:
        rows, cols = len(grid), len(grid[0])
        dirs = [(0, 1), (0, -1), (1, 0), (-1, 0)]
        dist = [[float("inf")] * cols for _ in range(rows)]
        dist[0][0] = 0
        dq = deque([(0, 0)])

        while dq:
            r, c = dq.popleft()
            for idx, (dr, dc) in enumerate(dirs, start=1):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols:
                    add = 0 if grid[r][c] == idx else 1
                    new_cost = dist[r][c] + add
                    if new_cost < dist[nr][nc]:
                        dist[nr][nc] = new_cost
                        if add == 0:
                            dq.appendleft((nr, nc))
                        else:
                            dq.append((nr, nc))
        return dist[rows - 1][cols - 1]
```
- **Time:** O(MN) — **Space:** O(MN)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((MN)!) | O(MN) |
| Better | O(MN log(MN)) | O(MN) |
| Optimal | O(MN) | O(MN) |

## Edge Cases & Pitfalls
- Directions are 1-indexed in the order right, left, down, up.
- A move leaving the grid is never followed; choose another move with cost if needed.
- Use 0-1 BFS only because edge weights are exactly `0` or `1`.

## Related
- 0-1 BFS
- Dijkstra on grids

