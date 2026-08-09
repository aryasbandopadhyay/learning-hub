# 06. Loud and Rich

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Implement `loudAndRich` for **Loud and Rich**. `quiet[i]` is person `i`'s quietness and `richer[j] = [a, b]` means `a` is richer than `b`. For each person `x`, return the quietest person among everyone known to be at least as rich as `x`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

Return `answer` ordered by person index, so `answer[i]` describes person `i`.

**Input**
- `richer`: list; richer-than relations.
- `quiet`: list; quietness scores.

**Output**
- A list. Return `answer` ordered by person index, so `answer[i]` describes person `i`.

## Constraints
- `1 <= len(quiet) <= 500`, `0 <= len(richer) <= 5000`

## Examples
```text
Input: richer = [[1,0],[2,1],[3,1]], quiet = [3,2,5,4]
Output: [1,1,2,3]
Explanation: Person 1 is quietest among people richer than or equal to 0 and 1. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Edges point from richer to poorer. Each person's answer is the minimum-quiet ancestor including itself. Dynamic programming can propagate richer answers down the DAG.

## Approach 1 — Naive / Brute Force
**Idea:** For every person, DFS through richer ancestors and choose the minimum quiet score.
```python
class Solution:
    def loudAndRich(self, richer: list[list[int]], quiet: list[int]) -> list[int]:
        n = len(quiet)
        parents = [[] for _ in range(n)]
        for a, b in richer:
            parents[b].append(a)
        ans = []
        for x in range(n):
            best = x; seen = {x}; stack = [x]
            while stack:
                u = stack.pop()
                if quiet[u] < quiet[best]:
                    best = u
                for p in parents[u]:
                    if p not in seen:
                        seen.add(p); stack.append(p)
            ans.append(best)
        return ans
```
- **Time:** O(n(n + e)) — **Space:** O(n + e)

## Approach 2 — Better
**Idea:** Memoized DFS computes the quietest richer-or-equal person once for each node.
```python
class Solution:
    def loudAndRich(self, richer: list[list[int]], quiet: list[int]) -> list[int]:
        n = len(quiet)
        parents = [[] for _ in range(n)]
        for a, b in richer:
            parents[b].append(a)
        memo = [-1] * n
        def dfs(x):
            if memo[x] != -1:
                return memo[x]
            best = x
            for p in parents[x]:
                cand = dfs(p)
                if quiet[cand] < quiet[best]:
                    best = cand
            memo[x] = best
            return best
        return [dfs(i) for i in range(n)]
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Approach 3 — Optimal
**Idea:** Topologically process richer sources first and relax each poorer node's answer.
```python
class Solution:
    def loudAndRich(self, richer: list[list[int]], quiet: list[int]) -> list[int]:
        from collections import deque
        n = len(quiet)
        g = [[] for _ in range(n)]
        indeg = [0] * n
        for a, b in richer:
            g[a].append(b); indeg[b] += 1
        ans = list(range(n))
        q = deque([i for i in range(n) if indeg[i] == 0])
        while q:
            u = q.popleft()
            for v in g[u]:
                if quiet[ans[u]] < quiet[ans[v]]:
                    ans[v] = ans[u]
                indeg[v] -= 1
                if indeg[v] == 0:
                    q.append(v)
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
- Edges must be interpreted richer-to-poorer.
- Standard inputs use distinct quiet values, making answers unique.

## Related
- Topological DP
- Ancestors in DAG
