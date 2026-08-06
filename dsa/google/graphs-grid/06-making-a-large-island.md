# 06. Making a Large Island

- **Difficulty:** Hard
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
Given an `n x n` binary grid, you may change at most one `0` to `1`. Return the largest 4-directional island size possible. Constraints: `1 <= n <= 500`.

## Examples
```text
Input: grid = [[1,0],[0,1]]
Output: 3
Explanation: Flipping either zero connects two one-cell islands through the flipped cell.
```

## Understanding & Intuition
Flipping a zero can merge all distinct islands adjacent to it. Recomputing components for every zero is expensive. Labeling or unioning components once lets each zero be evaluated by summing unique neighboring island sizes.

## Approach 1 — Naive / Brute Force
**Idea:** Try each possible flip and recompute the largest island.
```python
class Solution:
    def largestIsland(self, grid: list[list[int]]) -> int:
        n=len(grid)
        def area(g,r,c,seen):
            stack=[(r,c)]; seen.add((r,c)); total=0
            while stack:
                r,c=stack.pop(); total+=1
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr,nc=r+dr,c+dc
                    if 0<=nr<n and 0<=nc<n and g[nr][nc] and (nr,nc) not in seen:
                        seen.add((nr,nc)); stack.append((nr,nc))
            return total
        zeros=[(r,c) for r in range(n) for c in range(n) if grid[r][c]==0]
        if not zeros: return n*n
        best=1
        for zr,zc in zeros:
            g=[row[:] for row in grid]; g[zr][zc]=1; seen=set()
            for r in range(n):
                for c in range(n):
                    if g[r][c] and (r,c) not in seen: best=max(best,area(g,r,c,seen))
        return best
```
- **Time:** O(n^4) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Paint each island with an id and store its size.
```python
class Solution:
    def largestIsland(self, grid: list[list[int]]) -> int:
        n=len(grid); g=[row[:] for row in grid]; sizes={0:0}; label=2
        def paint(r,c,label):
            g[r][c]=label; stack=[(r,c)]; total=0
            while stack:
                r,c=stack.pop(); total+=1
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr,nc=r+dr,c+dc
                    if 0<=nr<n and 0<=nc<n and g[nr][nc]==1: g[nr][nc]=label; stack.append((nr,nc))
            return total
        for r in range(n):
            for c in range(n):
                if g[r][c]==1: sizes[label]=paint(r,c,label); label+=1
        best=max(sizes.values())
        for r in range(n):
            for c in range(n):
                if g[r][c]==0:
                    ids={g[nr][nc] for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)) for nr,nc in [(r+dr,c+dc)] if 0<=nr<n and 0<=nc<n}
                    best=max(best,1+sum(sizes[i] for i in ids))
        return best
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Union adjacent land cells, then evaluate every zero using unique neighboring roots.
```python
class Solution:
    def largestIsland(self, grid: list[list[int]]) -> int:
        n=len(grid); parent=list(range(n*n)); size=[1]*(n*n)
        def find(x):
            while parent[x]!=x: parent[x]=parent[parent[x]]; x=parent[x]
            return x
        def union(a,b):
            ra,rb=find(a),find(b)
            if ra==rb: return
            if size[ra]<size[rb]: ra,rb=rb,ra
            parent[rb]=ra; size[ra]+=size[rb]
        for r in range(n):
            for c in range(n):
                if grid[r][c]:
                    if r and grid[r-1][c]: union(r*n+c,(r-1)*n+c)
                    if c and grid[r][c-1]: union(r*n+c,r*n+c-1)
        best=0; has_zero=False
        for r in range(n):
            for c in range(n):
                if grid[r][c]: best=max(best,size[find(r*n+c)])
                else:
                    has_zero=True; roots=set()
                    for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                        nr,nc=r+dr,c+dc
                        if 0<=nr<n and 0<=nc<n and grid[nr][nc]: roots.add(find(nr*n+nc))
                    best=max(best,1+sum(size[x] for x in roots))
        return n*n if not has_zero else best
```
- **Time:** O(n^2 α(n^2)) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^4) | O(n^2) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2 α(n^2)) | O(n^2) |

## Edge Cases & Pitfalls
- All land returns `n*n`.
- Avoid counting the same island twice around a zero.
- Connectivity is 4-directional.

## Related
- Shortest Bridge
- Minimum Days to Disconnect Island
