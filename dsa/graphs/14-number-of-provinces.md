# 14. Number of Provinces

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an adjacency matrix of direct city connections, return number of connected provinces. Constraints: n <= 200.

## Examples
```text
Input: isConnected = [[1,1,0],[1,1,0],[0,0,1]]
Output: 2
Explanation: Cities 0 and 1 form one province; city 2 another.
```

## Understanding & Intuition
A province is a connected component in an undirected graph represented as a matrix.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def findCircleNum(self, isConnected: List[List[int]]) -> int:
        # DFS scans matrix rows for neighbors.
        n = len(isConnected); seen = set()
        def dfs(i):
            seen.add(i)
            for j in range(n):
                if isConnected[i][j] and j not in seen:
                    dfs(j)
        ans = 0
        for i in range(n):
            if i not in seen:
                ans += 1; dfs(i)
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def findCircleNum(self, isConnected: List[List[int]]) -> int:
        # DFS scans matrix rows for neighbors.
        n = len(isConnected); seen = set()
        def dfs(i):
            seen.add(i)
            for j in range(n):
                if isConnected[i][j] and j not in seen:
                    dfs(j)
        ans = 0
        for i in range(n):
            if i not in seen:
                ans += 1; dfs(i)
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def findCircleNum(self, isConnected: List[List[int]]) -> int:
        # DFS scans matrix rows for neighbors.
        n = len(isConnected); seen = set()
        def dfs(i):
            seen.add(i)
            for j in range(n):
                if isConnected[i][j] and j not in seen:
                    dfs(j)
        ans = 0
        for i in range(n):
            if i not in seen:
                ans += 1; dfs(i)
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
