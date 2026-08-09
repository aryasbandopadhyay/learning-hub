# 01. Parallel Courses

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Amazon, Google, Meta

## Problem
Implement `minimumSemesters` for **Parallel Courses**. Given `n` courses labeled `1..n` and prerequisite edges `relations`, where `[a, b]` means `a` before `b`, return the minimum semesters needed when any number of currently available courses may be taken each semester. Return `-1` if all courses cannot be completed.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `n`: integer; problem size or count as defined above.
- `relations`: list; prerequisite relations.

**Output**
- A single integer.

## Constraints
- `1 <= n <= 5000`, `0 <= len(relations) <= 20000`

## Examples
```text
Input: n = 3, relations = [[1,3],[2,3]]
Output: 2
Explanation: Take 1 and 2, then 3. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Courses in the same semester are exactly the zero-indegree frontier. A cycle leaves some courses with unsatisfied prerequisites forever. The minimum semesters equal the longest prerequisite chain in a DAG.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan all unfinished courses and take every course whose prerequisite set is already done.
```python
class Solution:
    def minimumSemesters(self, n: int, relations: list[list[int]]) -> int:
        pre = [set() for _ in range(n + 1)]
        for a, b in relations:
            pre[b].add(a)
        done = set()
        semesters = 0
        while len(done) < n:
            take = [c for c in range(1, n + 1) if c not in done and pre[c] <= done]
            if not take:
                return -1
            done.update(take)
            semesters += 1
        return semesters
```
- **Time:** O(n(n + e)) — **Space:** O(n + e)

## Approach 2 — Better
**Idea:** Kahn's algorithm by queue levels; each level consumes one semester.
```python
class Solution:
    def minimumSemesters(self, n: int, relations: list[list[int]]) -> int:
        from collections import deque
        g = [[] for _ in range(n + 1)]
        indeg = [0] * (n + 1)
        for a, b in relations:
            g[a].append(b); indeg[b] += 1
        q = deque([i for i in range(1, n + 1) if indeg[i] == 0])
        seen = sem = 0
        while q:
            sem += 1
            for _ in range(len(q)):
                u = q.popleft(); seen += 1
                for v in g[u]:
                    indeg[v] -= 1
                    if indeg[v] == 0:
                        q.append(v)
        return sem if seen == n else -1
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Approach 3 — Optimal
**Idea:** DFS returns the longest chain starting at each course while colors detect directed cycles.
```python
class Solution:
    def minimumSemesters(self, n: int, relations: list[list[int]]) -> int:
        g = [[] for _ in range(n + 1)]
        for a, b in relations:
            g[a].append(b)
        color = [0] * (n + 1)
        memo = [0] * (n + 1)
        def dfs(u):
            if color[u] == 1:
                return -1
            if color[u] == 2:
                return memo[u]
            color[u] = 1
            best = 1
            for v in g[u]:
                got = dfs(v)
                if got == -1:
                    return -1
                best = max(best, got + 1)
            color[u] = 2; memo[u] = best
            return best
        ans = 0
        for i in range(1, n + 1):
            got = dfs(i)
            if got == -1:
                return -1
            ans = max(ans, got)
        return ans
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n(n + e)) | O(n + e) |
| Better | O(n + e) | O(n + e) |
| Optimal | O(n + e) | O(n + e) |

## Edge Cases & Pitfalls
- A self-loop makes completion impossible.
- Course labels start at `1`.

## Related
- Course Schedule
- Topological Sorting
