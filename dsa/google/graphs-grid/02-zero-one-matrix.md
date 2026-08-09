# 02. 01 Matrix

- **Difficulty:** Medium
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given an `m x n` binary matrix `mat`.

For every cell, compute its distance to the nearest cell containing `0`. Distance is measured by 4-directional moves between adjacent cells.

**Input**
- `mat`: an `m x n` matrix containing only `0` and `1`.

**Output**
- An `m x n` distance matrix in the same row and column order as `mat`. **This judge compares exactly**, so preserve the input shape and ordering.

## Constraints
- `1 <= m, n <= 10^4`
- `1 <= m * n <= 10^4`
- `mat[r][c]` is `0` or `1`
- There is at least one `0` in `mat`.

## Examples
```text
Input: mat = [[0,0,0],[0,1,0],[1,1,1]]
Output: [[0,0,0],[0,1,0],[1,2,1]]
Explanation: Zero cells have distance `0`. The bottom-middle cell is two steps from the nearest zero, while the other bottom ones are one step away.
```

## Understanding & Intuition
A BFS from each `1` repeats the same wavefronts. Instead, start a BFS from all zeros simultaneously so each cell is reached at its nearest distance. A two-pass dynamic program also computes nearest-zero Manhattan distance.

## Approach 1 — Naive / Brute Force
**Idea:** Run BFS from every cell that contains `1`.
```python
class Solution:
    def updateMatrix(self, mat: list[list[int]]) -> list[list[int]]:
        from collections import deque
        m,n=len(mat),len(mat[0]); ans=[[0]*n for _ in range(m)]
        for sr in range(m):
            for sc in range(n):
                if mat[sr][sc]==0: continue
                q=deque([(sr,sc,0)]); seen={(sr,sc)}
                while q:
                    r,c,d=q.popleft()
                    if mat[r][c]==0: ans[sr][sc]=d; break
                    for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                        nr,nc=r+dr,c+dc
                        if 0<=nr<m and 0<=nc<n and (nr,nc) not in seen:
                            seen.add((nr,nc)); q.append((nr,nc,d+1))
        return ans
```
- **Time:** O((mn)^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Multi-source BFS from every zero.
```python
class Solution:
    def updateMatrix(self, mat: list[list[int]]) -> list[list[int]]:
        from collections import deque
        m,n=len(mat),len(mat[0]); dist=[[-1]*n for _ in range(m)]; q=deque()
        for r in range(m):
            for c in range(n):
                if mat[r][c]==0: dist[r][c]=0; q.append((r,c))
        while q:
            r,c=q.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and dist[nr][nc]<0:
                    dist[nr][nc]=dist[r][c]+1; q.append((nr,nc))
        return dist
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Relax distances in two sweeps.
```python
class Solution:
    def updateMatrix(self, mat: list[list[int]]) -> list[list[int]]:
        m,n=len(mat),len(mat[0]); inf=m+n+5
        dp=[[0 if mat[r][c]==0 else inf for c in range(n)] for r in range(m)]
        for r in range(m):
            for c in range(n):
                if r: dp[r][c]=min(dp[r][c],dp[r-1][c]+1)
                if c: dp[r][c]=min(dp[r][c],dp[r][c-1]+1)
        for r in range(m-1,-1,-1):
            for c in range(n-1,-1,-1):
                if r+1<m: dp[r][c]=min(dp[r][c],dp[r+1][c]+1)
                if c+1<n: dp[r][c]=min(dp[r][c],dp[r][c+1]+1)
        return dp
```
- **Time:** O(mn) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((mn)^2) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(mn) |

## Edge Cases & Pitfalls
- There is always at least one zero.
- Movement is 4-directional only.
- Initialize every zero before multi-source BFS.

## Related
- Map of Highest Peak
- As Far from Land as Possible
