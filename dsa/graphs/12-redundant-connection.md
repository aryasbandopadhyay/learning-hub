# 12. Redundant Connection

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
An undirected graph started as a tree on nodes `1` through `n`, then one extra edge was added. Edges are given in insertion order.

Return the edge that can be removed to make the graph a tree again. If multiple edges could be removed, return the one that appears last in the list.

**Input**
- `edges`: undirected edges `[u, v]` using 1-based labels.

**Output**
- The redundant edge as `[u, v]`. **This judge compares exactly**, so keep the endpoint order from `edges`.

## Constraints
- `n == edges.length`
- `3 <= n <= 1000`
- `edges[i].length == 2`
- `1 <= u < v <= n`
- The input contains exactly one extra edge beyond a tree.

## Examples
```text
Input: edges = [[1,2],[1,3],[2,3]]
Output: [2,3]
Explanation: After `[1,2]` and `[1,3]`, nodes 2 and 3 are already connected through 1. Adding `[2,3]` forms the cycle, so it is returned.
```

## Understanding & Intuition
The redundant edge is the first edge joining nodes in the same component.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def findRedundantConnection(self, edges: List[List[int]]) -> List[int]:
        # Union-find returns the edge that would form a cycle.
        parent = list(range(len(edges) + 1))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra == rb: return [a, b]
            parent[ra] = rb
        return []
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def findRedundantConnection(self, edges: List[List[int]]) -> List[int]:
        # Union-find returns the edge that would form a cycle.
        parent = list(range(len(edges) + 1))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra == rb: return [a, b]
            parent[ra] = rb
        return []
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def findRedundantConnection(self, edges: List[List[int]]) -> List[int]:
        # Union-find returns the edge that would form a cycle.
        parent = list(range(len(edges) + 1))
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra == rb: return [a, b]
            parent[ra] = rb
        return []
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
