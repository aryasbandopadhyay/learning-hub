# 11. Number of Connected Components in an Undirected Graph

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, LinkedIn

## Problem
You are given `n` nodes labeled `0` through `n - 1` and undirected edges.

Return the number of connected components. Two nodes are in the same component if some path connects them.

**Input**
- `n`: the number of nodes.
- `edges`: undirected edges `[u, v]`.

**Output**
- An integer: the number of connected components.

## Constraints
- `1 <= n <= 2000`
- `0 <= edges.length <= 5000`
- `edges[i].length == 2`
- `0 <= u, v < n`.

## Examples
```text
Input: n = 5, edges = [[0,1],[1,2],[3,4]]
Output: 2
Explanation: Nodes 0, 1, and 2 form one component, while nodes 3 and 4 form another. Therefore there are 2 components.
```

## Understanding & Intuition
Each component is a reachability set. Union endpoints and count remaining roots.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def countComponents(self, n: int, edges: List[List[int]]) -> int:
        # Union-find tracks how many sets remain.
        parent, size, count = list(range(n)), [1]*n, n
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra != rb:
                if size[ra] < size[rb]: ra, rb = rb, ra
                parent[rb] = ra; size[ra] += size[rb]; count -= 1
        return count
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def countComponents(self, n: int, edges: List[List[int]]) -> int:
        # Union-find tracks how many sets remain.
        parent, size, count = list(range(n)), [1]*n, n
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra != rb:
                if size[ra] < size[rb]: ra, rb = rb, ra
                parent[rb] = ra; size[ra] += size[rb]; count -= 1
        return count
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def countComponents(self, n: int, edges: List[List[int]]) -> int:
        # Union-find tracks how many sets remain.
        parent, size, count = list(range(n)), [1]*n, n
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        for a, b in edges:
            ra, rb = find(a), find(b)
            if ra != rb:
                if size[ra] < size[rb]: ra, rb = rb, ra
                parent[rb] = ra; size[ra] += size[rb]; count -= 1
        return count
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
