# 22. Find the Town Judge

- **Difficulty:** Easy
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
In a town of `n` people labeled `1` through `n`, the judge trusts nobody and every other person trusts the judge.

Given trust pairs `[a, b]`, return the judge's label, or `-1` if no valid judge exists.

**Input**
- `n`: the number of people.
- `trust`: directed pairs `[a, b]` meaning `a` trusts `b`.

**Output**
- The judge's label, or `-1` if none exists.

## Constraints
- `1 <= n <= 1000`
- `0 <= trust.length <= 10000`
- `trust[i].length == 2`
- `1 <= a, b <= n`
- `a != b`; trust pairs are unique.

## Examples
```text
Input: n = 3, trust = [[1,3],[2,3]]
Output: 3
Explanation: Persons 1 and 2 both trust person 3, and person 3 trusts nobody. Therefore 3 is the judge.
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
