# 20. All Paths From Source to Target

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
You are given a directed acyclic graph as an adjacency list. Nodes are `0` through `n - 1`, and `graph[i]` lists outgoing neighbours from `i`.

Return all paths from source node `0` to target node `n - 1`.

**Input**
- `graph`: an adjacency list for a DAG.

**Output**
- A list of paths from `0` to `n - 1`. **This judge compares exactly**, so return paths in depth-first order, following neighbours in the order they appear in `graph[node]`.

## Constraints
- `n == graph.length`
- `2 <= n <= 15`
- `0 <= graph[i][j] < n`
- The graph is acyclic.
- `graph[n - 1]` is empty.

## Examples
```text
Input: graph = [[1,2],[3],[3],[]]
Output: [[0,1,3],[0,2,3]]
Explanation: Following neighbour 1 first gives `[0,1,3]`. Following neighbour 2 next gives `[0,2,3]`, and no other source-to-target paths exist.
```

## Understanding & Intuition
The DAG property prevents cycles. Backtracking enumerates paths, and output size can be exponential.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def allPathsSourceTarget(self, graph: List[List[int]]) -> List[List[int]]:
        # Backtrack through all choices.
        target, ans = len(graph) - 1, []
        def dfs(node, path):
            if node == target:
                ans.append(path[:]); return
            for nei in graph[node]:
                path.append(nei); dfs(nei, path); path.pop()
        dfs(0, [0])
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def allPathsSourceTarget(self, graph: List[List[int]]) -> List[List[int]]:
        # Backtrack through all choices.
        target, ans = len(graph) - 1, []
        def dfs(node, path):
            if node == target:
                ans.append(path[:]); return
            for nei in graph[node]:
                path.append(nei); dfs(nei, path); path.pop()
        dfs(0, [0])
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def allPathsSourceTarget(self, graph: List[List[int]]) -> List[List[int]]:
        # Backtrack through all choices.
        target, ans = len(graph) - 1, []
        def dfs(node, path):
            if node == target:
                ans.append(path[:]); return
            for nei in graph[node]:
                path.append(nei); dfs(nei, path); path.pop()
        dfs(0, [0])
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
