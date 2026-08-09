# 10. Map of Highest Peak

- **Difficulty:** Medium
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given an `m x n` map `isWater`, where `1` marks water and `0` marks land.

Assign a non-negative height to every cell so that all water cells have height `0` and adjacent cells differ by at most `1`. Return a valid height matrix whose maximum height is as large as possible.

**Input**
- `isWater`: an `m x n` matrix containing only `0` and `1`.

**Output**
- An `m x n` height matrix in the same row and column order. **This judge compares exactly**, so return the canonical maximum-height assignment where each cell height is its 4-directional distance to the nearest water cell.

## Constraints
- `1 <= m, n <= 1000`
- `1 <= m * n <= 10^5`
- `isWater[r][c]` is `0` or `1`
- There is at least one water cell.

## Examples
```text
Input: isWater = [[0,1],[0,0]]
Output: [[1,0],[2,1]]
Explanation: The water cell has height `0`; adjacent land cells get height `1`, and the farthest land cell is two steps away.
```

## Understanding & Intuition
Every water cell has fixed height zero. To maximize heights without violating adjacent differences, each other cell can rise only to its nearest-water distance. Multi-source BFS or distance-transform sweeps compute this deterministically.

## Approach 1 — Naive / Brute Force
**Idea:** BFS from each land cell to the closest water.
```python
class Solution:
    def highestPeak(self, isWater: list[list[int]]) -> list[list[int]]:
        from collections import deque
        m,n=len(isWater),len(isWater[0]); ans=[[0]*n for _ in range(m)]
        for sr in range(m):
            for sc in range(n):
                if isWater[sr][sc]: continue
                q=deque([(sr,sc,0)]); seen={(sr,sc)}
                while q:
                    r,c,d=q.popleft()
                    if isWater[r][c]: ans[sr][sc]=d; break
                    for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                        nr,nc=r+dr,c+dc
                        if 0<=nr<m and 0<=nc<n and (nr,nc) not in seen: seen.add((nr,nc)); q.append((nr,nc,d+1))
        return ans
```
- **Time:** O((mn)^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** BFS from all water cells at once.
```python
class Solution:
    def highestPeak(self, isWater: list[list[int]]) -> list[list[int]]:
        from collections import deque
        m,n=len(isWater),len(isWater[0]); ans=[[-1]*n for _ in range(m)]; q=deque()
        for r in range(m):
            for c in range(n):
                if isWater[r][c]: ans[r][c]=0; q.append((r,c))
        while q:
            r,c=q.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and ans[nr][nc]<0: ans[nr][nc]=ans[r][c]+1; q.append((nr,nc))
        return ans
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Use two sweeps to compute nearest-water distances.
```python
class Solution:
    def highestPeak(self, isWater: list[list[int]]) -> list[list[int]]:
        m,n=len(isWater),len(isWater[0]); inf=m+n+5
        dp=[[0 if isWater[r][c] else inf for c in range(n)] for r in range(m)]
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
- Initialize every water cell to zero.
- The returned canonical grid is deterministic.
- Adjacent height differences follow from shortest distances.

## Related
- 01 Matrix
- As Far from Land as Possible
