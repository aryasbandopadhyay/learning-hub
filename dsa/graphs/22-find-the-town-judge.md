# 22. Find the Town Judge

- **Difficulty:** Easy
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Find the person trusted by everyone else and trusting nobody, or -1. People are labeled 1..n.

## Examples
```text
Input: n = 3, trust = [[1,3],[2,3]]
Output: 3
Explanation: Everyone except 3 trusts 3; 3 trusts nobody.
```

## Understanding & Intuition
The judge has indegree n-1 and outdegree 0. A net score captures both conditions.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def findJudge(self, n: int, trust: List[List[int]]) -> int:
        # Trusting subtracts one; being trusted adds one.
        score = [0] * (n + 1)
        for a, b in trust:
            score[a] -= 1; score[b] += 1
        for i in range(1, n + 1):
            if score[i] == n - 1:
                return i
        return -1
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def findJudge(self, n: int, trust: List[List[int]]) -> int:
        # Trusting subtracts one; being trusted adds one.
        score = [0] * (n + 1)
        for a, b in trust:
            score[a] -= 1; score[b] += 1
        for i in range(1, n + 1):
            if score[i] == n - 1:
                return i
        return -1
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def findJudge(self, n: int, trust: List[List[int]]) -> int:
        # Trusting subtracts one; being trusted adds one.
        score = [0] * (n + 1)
        for a, b in trust:
            score[a] -= 1; score[b] += 1
        for i in range(1, n + 1):
            if score[i] == n - 1:
                return i
        return -1
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
