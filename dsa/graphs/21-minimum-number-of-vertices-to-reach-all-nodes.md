# 21. Minimum Number of Vertices to Reach All Nodes

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
You are given a directed acyclic graph with `n` nodes labeled `0` through `n - 1`.

Return the smallest set of starting vertices from which every node is reachable. In a DAG, these are exactly the nodes with no incoming edges.

**Input**
- `n`: the number of nodes.
- `edges`: directed edges `[from, to]`.

**Output**
- A list of starting vertices. **This judge compares exactly**, so return zero-indegree nodes in increasing numeric order.

## Constraints
- `2 <= n <= 100000`
- `0 <= edges.length <= min(100000, n * (n - 1) / 2)`
- `edges[i].length == 2`
- `0 <= from, to < n`
- The graph is a DAG.

## Examples
```text
Input: n = 6, edges = [[0,1],[0,2],[2,5],[3,4],[4,2]]
Output: [0,3]
Explanation: Nodes 0 and 3 have no incoming edges, so they must be chosen. From them, every other node is reachable, and the sorted output is `[0,3]`.
```

## Understanding & Intuition
Zero-indegree nodes cannot be reached by any other node, so all are necessary. In a DAG they are also sufficient.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def findSmallestSetOfVertices(self, n: int, edges: List[List[int]]) -> List[int]:
        # Return nodes with no incoming edges.
        incoming = {v for _, v in edges}
        return [i for i in range(n) if i not in incoming]
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def findSmallestSetOfVertices(self, n: int, edges: List[List[int]]) -> List[int]:
        # Return nodes with no incoming edges.
        incoming = {v for _, v in edges}
        return [i for i in range(n) if i not in incoming]
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def findSmallestSetOfVertices(self, n: int, edges: List[List[int]]) -> List[int]:
        # Return nodes with no incoming edges.
        incoming = {v for _, v in edges}
        return [i for i in range(n) if i not in incoming]
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
