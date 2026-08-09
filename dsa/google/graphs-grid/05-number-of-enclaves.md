# 05. Number of Enclaves

- **Difficulty:** Medium
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given an `m x n` binary grid where `1` is land and `0` is sea.

From a land cell, you may move 4-directionally to adjacent land. Return the number of land cells from which it is impossible to walk off the grid boundary.

**Input**
- `grid`: an `m x n` matrix containing only `0` and `1`.

**Output**
- The number of land cells not connected to any boundary land cell.

## Constraints
- `1 <= m, n <= 500`
- `grid[r][c]` is `0` or `1`

## Examples
```text
Input: grid = [[0,0,0,0],[1,0,1,0],[0,1,1,0],[0,0,0,0]]
Output: 3
Explanation: The three interior land cells are surrounded away from the boundary and cannot leave; the boundary land cell is not counted.
```

## Understanding & Intuition
A land component is an enclave exactly when it does not touch the boundary. You can inspect each component, or remove every boundary-reachable component first. A DSU with a virtual ocean models the same idea.

## Approach 1 — Naive / Brute Force
**Idea:** DFS each land component and count it only if it does not touch the boundary.
```python
class Solution:
    def numEnclaves(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0]); seen=[[False]*n for _ in range(m)]
        def dfs(r,c,cells):
            seen[r][c]=True; cells.append((r,c)); touch=r in (0,m-1) or c in (0,n-1)
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and grid[nr][nc] and not seen[nr][nc]: touch=dfs(nr,nc,cells) or touch
            return touch
        ans=0
        for r in range(m):
            for c in range(n):
                if grid[r][c] and not seen[r][c]:
                    cells=[]
                    if not dfs(r,c,cells): ans+=len(cells)
        return ans
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Flood-fill boundary land to water, then count what remains.
```python
class Solution:
    def numEnclaves(self, grid: list[list[int]]) -> int:
        from collections import deque
        g=[row[:] for row in grid]; m,n=len(g),len(g[0]); q=deque()
        for r in range(m):
            for c in (0,n-1):
                if g[r][c]: g[r][c]=0; q.append((r,c))
        for c in range(n):
            for r in (0,m-1):
                if g[r][c]: g[r][c]=0; q.append((r,c))
        while q:
            r,c=q.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and g[nr][nc]: g[nr][nc]=0; q.append((nr,nc))
        return sum(map(sum,g))
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Union land cells, connecting boundary land to a virtual ocean.
```python
class Solution:
    def numEnclaves(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0]); ocean=m*n; parent=list(range(ocean+1)); size=[1]*(ocean+1)
        def find(x):
            while parent[x]!=x: parent[x]=parent[parent[x]]; x=parent[x]
            return x
        def union(a,b):
            ra,rb=find(a),find(b)
            if ra==rb: return
            if size[ra]<size[rb]: ra,rb=rb,ra
            parent[rb]=ra; size[ra]+=size[rb]
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    i=r*n+c
                    if r in (0,m-1) or c in (0,n-1): union(i,ocean)
                    if r and grid[r-1][c]: union(i,(r-1)*n+c)
                    if c and grid[r][c-1]: union(i,r*n+c-1)
        root=find(ocean)
        return sum(1 for r in range(m) for c in range(n) if grid[r][c] and find(r*n+c)!=root)
```
- **Time:** O(mn α(mn)) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn α(mn)) | O(mn) |

## Edge Cases & Pitfalls
- Boundary land is never an enclave.
- Only land-connected paths matter.
- Count cells, not components.

## Related
- Shortest Bridge
- Minimum Days to Disconnect Island
