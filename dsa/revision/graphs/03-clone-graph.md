# 03. Clone Graph

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given a reference to a node in a connected undirected graph, return a **deep copy** of the entire graph.

Each graph node has an integer value and a list of neighboring nodes. The copied graph must contain new node objects with the same values and the same neighbor relationships, but no copied node may be the same object as an original node. If the input node is `None`, return `None`.

**Input**
- `node`: a reference to a graph node, or `None`. In examples, the graph may be shown as an adjacency list where node values are `1`-indexed.

**Output**
- The root node of a deep-copied graph with identical adjacency structure.

## Constraints
- The number of nodes is in the range `[0, 100]`.
- Node values are unique and usually match their `1`-indexed position in the adjacency list.
- The graph is undirected and connected when `node` is not `None`.
- There are no repeated neighbors for a node and no self-loops.

## Examples
```text
Input: adjList = [[2,4],[1,3],[2,4],[1,3]]
Output: deep copy of the same adjacency list
Explanation: The four-node cycle is recreated with four new nodes. Each copied node points to copied neighbors with the same values, so the serialized adjacency list matches the original.
```

```text
Input: adjList = []
Output: []
Explanation: An empty graph has no node to clone, so the result is empty.
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
