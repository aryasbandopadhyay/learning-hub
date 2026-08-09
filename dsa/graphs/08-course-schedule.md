# 08. Course Schedule

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
There are `numCourses` courses labeled `0` to `numCourses - 1`. Each pair `[course, prerequisite]` means `prerequisite` must be taken before `course`.

Return whether it is possible to finish all courses. This is possible exactly when the prerequisite graph has no directed cycle.

**Input**
- `numCourses`: the number of courses.
- `prerequisites`: directed pairs `[course, prerequisite]`.

**Output**
- `True` if all courses can be completed; otherwise `False`.

## Constraints
- `1 <= numCourses <= 2000`
- `0 <= prerequisites.length <= 5000`
- `prerequisites[i].length == 2`
- `0 <= course, prerequisite < numCourses`.

## Examples
```text
Input: numCourses = 2, prerequisites = [[1,0]]
Output: True
Explanation: Course 0 can be taken first. That unlocks course 1, so all courses can be completed.
```

## Understanding & Intuition
The prerequisite graph must be acyclic. Kahn's algorithm removes courses with zero remaining prerequisites.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def canFinish(self, numCourses: int, prerequisites: List[List[int]]) -> bool:
        # Topological sort detects cycles.
        graph = [[] for _ in range(numCourses)]
        indeg = [0] * numCourses
        for c, p in prerequisites:
            graph[p].append(c); indeg[c] += 1
        q = deque([i for i,d in enumerate(indeg) if d == 0])
        seen = 0
        while q:
            node = q.popleft(); seen += 1
            for nei in graph[node]:
                indeg[nei] -= 1
                if indeg[nei] == 0: q.append(nei)
        return seen == numCourses
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def canFinish(self, numCourses: int, prerequisites: List[List[int]]) -> bool:
        # Topological sort detects cycles.
        graph = [[] for _ in range(numCourses)]
        indeg = [0] * numCourses
        for c, p in prerequisites:
            graph[p].append(c); indeg[c] += 1
        q = deque([i for i,d in enumerate(indeg) if d == 0])
        seen = 0
        while q:
            node = q.popleft(); seen += 1
            for nei in graph[node]:
                indeg[nei] -= 1
                if indeg[nei] == 0: q.append(nei)
        return seen == numCourses
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def canFinish(self, numCourses: int, prerequisites: List[List[int]]) -> bool:
        # Topological sort detects cycles.
        graph = [[] for _ in range(numCourses)]
        indeg = [0] * numCourses
        for c, p in prerequisites:
            graph[p].append(c); indeg[c] += 1
        q = deque([i for i,d in enumerate(indeg) if d == 0])
        seen = 0
        while q:
            node = q.popleft(); seen += 1
            for nei in graph[node]:
                indeg[nei] -= 1
                if indeg[nei] == 0: q.append(nei)
        return seen == numCourses
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
