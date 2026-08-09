# 02. Max Area of Island

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Bloomberg, Microsoft

## Problem
You are given an `m x n` binary grid where `1` represents land and `0` represents water.

The area of an island is the number of land cells in one 4-directionally connected component. Return the largest island area, or `0` if there is no land.

**Input**
- `grid`: a 2-D list of integers with values `0` or `1`.

**Output**
- An integer: the maximum number of cells in any island.

## Constraints
- `m == grid.length`
- `n == grid[r].length`
- `1 <= m, n <= 50`
- `grid[r][c]` is either `0` or `1`.

## Examples
```text
Input: grid = [[0,1,0],[1,1,0],[0,0,1]]
Output: 3
Explanation: The cells `(0,1)`, `(1,0)`, and `(1,1)` touch by edges, making an island of area 3. The remaining land cell is separate, so the maximum area is 3.
```

## Understanding & Intuition
Area is component size. Efficient solutions ensure every cell is counted once.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def maxAreaOfIsland(self, grid: List[List[int]]) -> int:
        # In-place DFS counts and erases one island.
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] == 0:
                return 0
            grid[r][c] = 0
            return 1 + dfs(r+1,c) + dfs(r-1,c) + dfs(r,c+1) + dfs(r,c-1)
        best = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    best = max(best, dfs(r, c))
        return best
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def maxAreaOfIsland(self, grid: List[List[int]]) -> int:
        # In-place DFS counts and erases one island.
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] == 0:
                return 0
            grid[r][c] = 0
            return 1 + dfs(r+1,c) + dfs(r-1,c) + dfs(r,c+1) + dfs(r,c-1)
        best = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    best = max(best, dfs(r, c))
        return best
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def maxAreaOfIsland(self, grid: List[List[int]]) -> int:
        # In-place DFS counts and erases one island.
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] == 0:
                return 0
            grid[r][c] = 0
            return 1 + dfs(r+1,c) + dfs(r-1,c) + dfs(r,c+1) + dfs(r,c-1)
        best = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    best = max(best, dfs(r, c))
        return best
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V+E) or O(mn) | O(V) or O(mn) |
| Better | O(V+E) or O(mn) | O(V) or O(mn) |
| Optimal | O(V+E) or O(mn) | O(V) or O(mn) |

## Edge Cases & Pitfalls
- Empty or singleton graphs/grids.
- Mark visited before repeated traversal creates cycles.
- Preserve required in-place behavior when the signature returns None.

## Related
- BFS
- DFS
- Union-Find / Topological Sort
