# 21. Minimum Number of Vertices to Reach All Nodes

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
In a DAG, return the smallest set of starting vertices from which all nodes are reachable. Constraints: n <= 1e5.

## Examples
```text
Input: n = 6, edges = [[0,1],[0,2],[2,5],[3,4],[4,2]]
Output: [0,3]
Explanation: Only zero-indegree nodes must be chosen.
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
