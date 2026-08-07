# 16. Friend Circles / Number of Provinces

- **Difficulty:** Medium
- **Pattern:** Graph / Union-Find
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Given an adjacency matrix of connected cities, return the number of connected components, or provinces.

## Examples
```text
Input: isConnected = [[1,1,0],[1,1,0],[0,0,1]]
Output: 2
Explanation: Cities 0 and 1 form one province; city 2 forms another.
```

## Understanding & Intuition
The matrix is an undirected graph. Counting provinces is counting connected components.

## Approach 1 — Naive / Brute Force
**Idea:** Run DFS from each unvisited city.
```python
class Solution:
    def findCircleNum(self, isConnected: list[list[int]]) -> int:
        n = len(isConnected); seen = [False] * n
        def dfs(i: int) -> None:
            seen[i] = True
            for j in range(n):
                if isConnected[i][j] and not seen[j]: dfs(j)
        ans = 0
        for i in range(n):
            if not seen[i]: ans += 1; dfs(i)
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use iterative BFS to avoid recursion depth issues.
```python
from collections import deque
class Solution:
    def findCircleNum(self, isConnected: list[list[int]]) -> int:
        n = len(isConnected); seen = [False] * n; ans = 0
        for i in range(n):
            if seen[i]: continue
            ans += 1; seen[i] = True; q = deque([i])
            while q:
                city = q.popleft()
                for nei, ok in enumerate(isConnected[city]):
                    if ok and not seen[nei]: seen[nei] = True; q.append(nei)
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Union connected city pairs and decrement component count on successful unions.
```python
class Solution:
    def findCircleNum(self, isConnected: list[list[int]]) -> int:
        n = len(isConnected); parent = list(range(n)); rank = [0] * n
        def find(x: int) -> int:
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        def union(a: int, b: int) -> bool:
            ra, rb = find(a), find(b)
            if ra == rb: return False
            if rank[ra] < rank[rb]: ra, rb = rb, ra
            parent[rb] = ra
            if rank[ra] == rank[rb]: rank[ra] += 1
            return True
        ans = n
        for i in range(n):
            for j in range(i + 1, n):
                if isConnected[i][j] and union(i, j): ans -= 1
        return ans
```
- **Time:** O(n^2 α(n)) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n^2 α(n)) | O(n) |

## Edge Cases & Pitfalls
- Diagonal self-connections do not reduce the count.
- Matrix is symmetric.
- A single city is one province.

## Related
- Number of Connected Components in an Undirected Graph
- Graph Valid Tree
