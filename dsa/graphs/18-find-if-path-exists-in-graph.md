# 18. Find if Path Exists in Graph

- **Difficulty:** Easy
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
You are given an undirected graph with `n` nodes labeled `0` through `n - 1`, an edge list, a `source`, and a `destination`.

Return whether any path exists from `source` to `destination`.

**Input**
- `n`: the number of nodes.
- `edges`: undirected edges `[u, v]`.
- `source`: the start node.
- `destination`: the target node.

**Output**
- `True` if `destination` is reachable from `source`; otherwise `False`.

## Constraints
- `1 <= n <= 200000`
- `0 <= edges.length <= 200000`
- `edges[i].length == 2`
- `0 <= u, v, source, destination < n`.

## Examples
```text
Input: n = 3, edges = [[0,1],[1,2]], source = 0, destination = 2
Output: True
Explanation: Node 0 connects to node 1, and node 1 connects to node 2. Therefore a path from 0 to 2 exists.
```

## Understanding & Intuition
Path existence is connectivity. Union-find merges edges and compares source and destination roots.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def validPath(self, n: int, edges: List[List[int]], source: int, destination: int) -> bool:
        # Union all edges, then compare roots.
        parent = list(range(n))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            parent[find(a)] = find(b)
        return find(source) == find(destination)
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def validPath(self, n: int, edges: List[List[int]], source: int, destination: int) -> bool:
        # Union all edges, then compare roots.
        parent = list(range(n))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            parent[find(a)] = find(b)
        return find(source) == find(destination)
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def validPath(self, n: int, edges: List[List[int]], source: int, destination: int) -> bool:
        # Union all edges, then compare roots.
        parent = list(range(n))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            parent[find(a)] = find(b)
        return find(source) == find(destination)
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
