# 17. Is Graph Bipartite?

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Given an undirected graph adjacency list, return whether it can be split into two independent sets. Constraints: n <= 100.

## Examples
```text
Input: graph = [[1,3],[0,2],[1,3],[0,2]]
Output: True
Explanation: Alternating colors work.
```

## Understanding & Intuition
A graph is bipartite iff every component can be colored with two colors without conflicts.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def isBipartite(self, graph: List[List[int]]) -> bool:
        # BFS two-colors every component.
        color = [-1] * len(graph)
        for s in range(len(graph)):
            if color[s] != -1: continue
            q = deque([s]); color[s] = 0
            while q:
                u = q.popleft()
                for v in graph[u]:
                    if color[v] == -1:
                        color[v] = 1 - color[u]; q.append(v)
                    elif color[v] == color[u]:
                        return False
        return True
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def isBipartite(self, graph: List[List[int]]) -> bool:
        # BFS two-colors every component.
        color = [-1] * len(graph)
        for s in range(len(graph)):
            if color[s] != -1: continue
            q = deque([s]); color[s] = 0
            while q:
                u = q.popleft()
                for v in graph[u]:
                    if color[v] == -1:
                        color[v] = 1 - color[u]; q.append(v)
                    elif color[v] == color[u]:
                        return False
        return True
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def isBipartite(self, graph: List[List[int]]) -> bool:
        # BFS two-colors every component.
        color = [-1] * len(graph)
        for s in range(len(graph)):
            if color[s] != -1: continue
            q = deque([s]); color[s] = 0
            while q:
                u = q.popleft()
                for v in graph[u]:
                    if color[v] == -1:
                        color[v] = 1 - color[u]; q.append(v)
                    elif color[v] == color[u]:
                        return False
        return True
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
