# 04. Shortest Bridge

- **Difficulty:** Medium
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given an `n x n` binary grid containing exactly two islands. An island is a 4-directionally connected group of `1` cells.

You may change water cells (`0`) into land. Return the minimum number of water cells that must be changed to connect the two islands.

**Input**
- `grid`: an `n x n` matrix containing only `0` and `1`, with exactly two islands.

**Output**
- The minimum number of water cells to flip.

## Constraints
- `2 <= n <= 100`
- `grid[r][c]` is `0` or `1`
- The grid contains exactly two 4-directionally connected islands.

## Examples
```text
Input: grid = [[0,1],[1,0]]
Output: 1
Explanation: Flipping either water cell between the two diagonal land cells creates a 4-directional bridge, so one flip is enough.
```

## Understanding & Intuition
First identify the two islands. Comparing all pairs gives a direct but slow bridge length. Expanding BFS from one island reaches the other after the fewest water flips.

## Approach 1 — Naive / Brute Force
**Idea:** Collect both islands and minimize Manhattan distance minus one over all pairs.
```python
class Solution:
    def shortestBridge(self, grid: list[list[int]]) -> int:
        n=len(grid); seen=[[False]*n for _ in range(n)]; islands=[]
        def dfs(r,c,cells):
            seen[r][c]=True; cells.append((r,c))
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n and grid[nr][nc] and not seen[nr][nc]: dfs(nr,nc,cells)
        for r in range(n):
            for c in range(n):
                if grid[r][c] and not seen[r][c]:
                    cells=[]; dfs(r,c,cells); islands.append(cells)
        return min(abs(a-c)+abs(b-d)-1 for a,b in islands[0] for c,d in islands[1])
```
- **Time:** O(n^4) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Mark one island, then BFS through water until the second island is reached.
```python
class Solution:
    def shortestBridge(self, grid: list[list[int]]) -> int:
        from collections import deque
        n=len(grid); g=[row[:] for row in grid]; q=deque()
        def dfs(r,c):
            g[r][c]=2; q.append((r,c,0))
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n and g[nr][nc]==1: dfs(nr,nc)
        done=False
        for r in range(n):
            if done: break
            for c in range(n):
                if g[r][c]==1: dfs(r,c); done=True; break
        while q:
            r,c,d=q.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n:
                    if g[nr][nc]==1: return d
                    if g[nr][nc]==0: g[nr][nc]=2; q.append((nr,nc,d+1))
        return -1
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Bidirectionally expand from both islands until the frontiers touch.
```python
class Solution:
    def shortestBridge(self, grid: list[list[int]]) -> int:
        n=len(grid); seen=[[0]*n for _ in range(n)]; islands=[set(),set()]
        def dfs(r,c,label):
            seen[r][c]=label; islands[label-1].add((r,c))
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n and grid[nr][nc] and seen[nr][nc]==0: dfs(nr,nc,label)
        label=1
        for r in range(n):
            for c in range(n):
                if grid[r][c] and seen[r][c]==0: dfs(r,c,label); label+=1
        f1,f2=islands; v1=set(f1); v2=set(f2); dist=0
        while f1:
            if len(f1)>len(f2): f1,f2,v1,v2=f2,f1,v2,v1
            nxt=set()
            for r,c in f1:
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nb=(r+dr,c+dc); nr,nc=nb
                    if 0<=nr<n and 0<=nc<n:
                        if nb in v2: return dist
                        if nb not in v1: v1.add(nb); nxt.add(nb)
            f1=nxt; dist+=1
        return -1
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^4) | O(n^2) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- Islands use 4-directional connectivity only.
- Return flipped water cells, not total path length.
- Mark the first island before BFS.

## Related
- Making a Large Island
- Number of Enclaves
