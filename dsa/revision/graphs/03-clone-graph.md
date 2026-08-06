# 03. Clone Graph

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given a node in a connected undirected graph, return a deep copy. Node values are unique and the graph may contain cycles.

## Examples
```text
Input: adjList = [[2,4],[1,3],[2,4],[1,3]]
Output: deep copy of the same adjacency list
Explanation: Every original node is copied once.
```

## Understanding & Intuition
Cycles require memoization. Map original nodes to cloned nodes before cloning neighbors.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import Optional

class Node:
    def __init__(self, val: int = 0, neighbors: list['Node'] | None = None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []

class Solution:
    def cloneGraph(self, node: Optional['Node']) -> Optional['Node']:
        # DFS memoizes clones to handle cycles.
        clones = {}
        def clone(cur):
            if cur is None:
                return None
            if cur in clones:
                return clones[cur]
            clones[cur] = Node(cur.val)
            clones[cur].neighbors = [clone(nei) for nei in cur.neighbors]
            return clones[cur]
        return clone(node)
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import Optional

class Node:
    def __init__(self, val: int = 0, neighbors: list['Node'] | None = None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []

class Solution:
    def cloneGraph(self, node: Optional['Node']) -> Optional['Node']:
        # DFS memoizes clones to handle cycles.
        clones = {}
        def clone(cur):
            if cur is None:
                return None
            if cur in clones:
                return clones[cur]
            clones[cur] = Node(cur.val)
            clones[cur].neighbors = [clone(nei) for nei in cur.neighbors]
            return clones[cur]
        return clone(node)
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import Optional

class Node:
    def __init__(self, val: int = 0, neighbors: list['Node'] | None = None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []

class Solution:
    def cloneGraph(self, node: Optional['Node']) -> Optional['Node']:
        # DFS memoizes clones to handle cycles.
        clones = {}
        def clone(cur):
            if cur is None:
                return None
            if cur in clones:
                return clones[cur]
            clones[cur] = Node(cur.val)
            clones[cur].neighbors = [clone(nei) for nei in cur.neighbors]
            return clones[cur]
        return clone(node)
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
