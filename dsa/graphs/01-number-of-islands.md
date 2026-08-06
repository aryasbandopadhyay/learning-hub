# 01. Number of Islands

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an m x n grid of '1' land and '0' water, count 4-directionally connected islands. Constraints: 1 <= m,n <= 300.

## Examples
```text
Input: grid = [["1","1","0"],["0","1","0"],["1","0","1"]]
Output: 3
Explanation: Three separate land components exist.
```

## Understanding & Intuition
An island is a connected component in a grid graph. Once counted, all reachable land cells must be marked visited.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def numIslands(self, grid: List[List[str]]) -> int:
        # DFS sinks each discovered island.
        if not grid or not grid[0]:
            return 0
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] != "1":
                return
            grid[r][c] = "0"
            dfs(r+1,c); dfs(r-1,c); dfs(r,c+1); dfs(r,c-1)
        ans = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c] == "1":
                    ans += 1
                    dfs(r, c)
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def numIslands(self, grid: List[List[str]]) -> int:
        # DFS sinks each discovered island.
        if not grid or not grid[0]:
            return 0
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] != "1":
                return
            grid[r][c] = "0"
            dfs(r+1,c); dfs(r-1,c); dfs(r,c+1); dfs(r,c-1)
        ans = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c] == "1":
                    ans += 1
                    dfs(r, c)
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def numIslands(self, grid: List[List[str]]) -> int:
        # DFS sinks each discovered island.
        if not grid or not grid[0]:
            return 0
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] != "1":
                return
            grid[r][c] = "0"
            dfs(r+1,c); dfs(r-1,c); dfs(r,c+1); dfs(r,c-1)
        ans = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c] == "1":
                    ans += 1
                    dfs(r, c)
        return ans
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
