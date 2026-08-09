# 10. Graph Valid Tree

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
You are given `n` nodes labeled `0` through `n - 1` and undirected edges.

Return whether the edges form one valid tree. A tree is connected and acyclic, so every node is reachable and no cycle exists.

**Input**
- `n`: the number of nodes.
- `edges`: undirected edges `[u, v]`.

**Output**
- `True` if the graph is a valid tree; otherwise `False`.

## Constraints
- `1 <= n <= 2000`
- `0 <= edges.length <= 5000`
- `edges[i].length == 2`
- `0 <= u, v < n`.

## Examples
```text
Input: n = 5, edges = [[0,1],[0,2],[0,3],[1,4]]
Output: True
Explanation: All five nodes are connected, and the four edges do not create a cycle. A connected graph with `n - 1` acyclic edges is a tree.
```

## Understanding & Intuition
A tree has exactly n-1 edges and one connected component. Union-find also rejects cycles.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def validTree(self, n: int, edges: List[List[int]]) -> bool:
        # n-1 edges plus no union-find cycle implies a tree.
        if len(edges) != n - 1: return False
        parent = list(range(n))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra == rb: return False
            parent[ra] = rb
        return True
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def validTree(self, n: int, edges: List[List[int]]) -> bool:
        # n-1 edges plus no union-find cycle implies a tree.
        if len(edges) != n - 1: return False
        parent = list(range(n))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra == rb: return False
            parent[ra] = rb
        return True
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def validTree(self, n: int, edges: List[List[int]]) -> bool:
        # n-1 edges plus no union-find cycle implies a tree.
        if len(edges) != n - 1: return False
        parent = list(range(n))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra == rb: return False
            parent[ra] = rb
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
