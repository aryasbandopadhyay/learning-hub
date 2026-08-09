# 13. Unique Paths III

- **Difficulty:** Hard
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given a grid with one start square (`1`), one end square (`2`), empty squares (`0`), and obstacles (`-1`).

Return the number of 4-directional paths that start at `1`, end at `2`, and visit every non-obstacle square exactly once.

**Input**
- `grid`: an `m x n` matrix with values `-1`, `0`, `1`, and `2`.

**Output**
- The number of valid paths that cover every walkable square exactly once.

## Constraints
- `1 <= m, n <= 20`
- `1 <= m * n <= 20`
- `grid` contains exactly one `1` and exactly one `2`.

## Examples
```text
Input: grid = [[1,0,0,0],[0,0,0,0],[0,0,2,-1]]
Output: 2
Explanation: There are exactly two routes from start to end that visit every empty square once and avoid the obstacle.
```

## Understanding & Intuition
This is Hamiltonian path counting on a small grid. DFS tracks which cells have already been used and accepts only paths that reach the end after covering all open cells. Bitmask memoization can reuse repeated `(cell, visited)` states.

## Approach 1 — Naive / Brute Force
**Idea:** DFS with a copied visited set at each branch.
```python
class Solution:
    def uniquePathsIII(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0]); total=0; start=end=None
        for r in range(m):
            for c in range(n):
                if grid[r][c]!=-1: total+=1
                if grid[r][c]==1: start=(r,c)
                if grid[r][c]==2: end=(r,c)
        def dfs(r,c,seen):
            if (r,c)==end: return 1 if len(seen)==total else 0
            ans=0
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=-1 and (nr,nc) not in seen: ans+=dfs(nr,nc,seen|{(nr,nc)})
            return ans
        return dfs(start[0],start[1],{start})
```
- **Time:** O(4^K) — **Space:** O(K^2)

## Approach 2 — Better
**Idea:** Encode visited cells as a bitmask and memoize states.
```python
class Solution:
    def uniquePathsIII(self, grid: list[list[int]]) -> int:
        from functools import lru_cache
        m,n=len(grid),len(grid[0]); idx={}; start=end=None
        for r in range(m):
            for c in range(n):
                if grid[r][c]!=-1: idx[(r,c)]=len(idx)
                if grid[r][c]==1: start=(r,c)
                if grid[r][c]==2: end=(r,c)
        full=(1<<len(idx))-1
        @lru_cache(None)
        def dp(r,c,mask):
            if (r,c)==end: return 1 if mask==full else 0
            ans=0
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nb=(r+dr,c+dc)
                if nb in idx:
                    bit=1<<idx[nb]
                    if not mask&bit: ans+=dp(nb[0],nb[1],mask|bit)
            return ans
        return dp(start[0],start[1],1<<idx[start])
```
- **Time:** O(K 2^K) — **Space:** O(K 2^K)

## Approach 3 — Optimal
**Idea:** Backtrack in place with a remaining-cell counter.
```python
class Solution:
    def uniquePathsIII(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0]); remain=0; sr=sc=0
        for r in range(m):
            for c in range(n):
                if grid[r][c]!=-1: remain+=1
                if grid[r][c]==1: sr,sc=r,c
        def dfs(r,c,left):
            if grid[r][c]==2: return 1 if left==1 else 0
            old=grid[r][c]; grid[r][c]=-1; ans=0
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=-1: ans+=dfs(nr,nc,left-1)
            grid[r][c]=old
            return ans
        return dfs(sr,sc,remain)
```
- **Time:** O(4^K) — **Space:** O(K)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(4^K) | O(K^2) |
| Better | O(K 2^K) | O(K 2^K) |
| Optimal | O(4^K) | O(K) |

## Edge Cases & Pitfalls
- The end is valid only after all non-obstacle cells are visited.
- Obstacles cannot be entered.
- Restore cells after in-place backtracking.

## Related
- Word Search
- Nearest Exit from Entrance in Maze
