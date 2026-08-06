# 09. Course Schedule II

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Return one valid course order, or [] if impossible. Constraints: courses <= 2000.

## Examples
```text
Input: numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
Output: [0,1,2,3]
Explanation: Prerequisites appear before dependent courses.
```

## Understanding & Intuition
This is a topological ordering problem. If a cycle exists, no ordering contains all courses.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def findOrder(self, numCourses: int, prerequisites: List[List[int]]) -> List[int]:
        # Kahn's algorithm emits zero-indegree courses.
        graph = [[] for _ in range(numCourses)]
        indeg = [0] * numCourses
        for c, p in prerequisites:
            graph[p].append(c); indeg[c] += 1
        q = deque([i for i,d in enumerate(indeg) if d == 0])
        order = []
        while q:
            node = q.popleft(); order.append(node)
            for nei in graph[node]:
                indeg[nei] -= 1
                if indeg[nei] == 0: q.append(nei)
        return order if len(order) == numCourses else []
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def findOrder(self, numCourses: int, prerequisites: List[List[int]]) -> List[int]:
        # Kahn's algorithm emits zero-indegree courses.
        graph = [[] for _ in range(numCourses)]
        indeg = [0] * numCourses
        for c, p in prerequisites:
            graph[p].append(c); indeg[c] += 1
        q = deque([i for i,d in enumerate(indeg) if d == 0])
        order = []
        while q:
            node = q.popleft(); order.append(node)
            for nei in graph[node]:
                indeg[nei] -= 1
                if indeg[nei] == 0: q.append(nei)
        return order if len(order) == numCourses else []
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def findOrder(self, numCourses: int, prerequisites: List[List[int]]) -> List[int]:
        # Kahn's algorithm emits zero-indegree courses.
        graph = [[] for _ in range(numCourses)]
        indeg = [0] * numCourses
        for c, p in prerequisites:
            graph[p].append(c); indeg[c] += 1
        q = deque([i for i,d in enumerate(indeg) if d == 0])
        order = []
        while q:
            node = q.popleft(); order.append(node)
            for nei in graph[node]:
                indeg[nei] -= 1
                if indeg[nei] == 0: q.append(nei)
        return order if len(order) == numCourses else []
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
