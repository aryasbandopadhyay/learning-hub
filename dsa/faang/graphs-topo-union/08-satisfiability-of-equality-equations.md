# 08. Satisfiability of Equality Equations

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Amazon, Meta

## Problem
Implement `equationsPossible` for **Satisfiability of Equality Equations**. Given equations between lowercase variables, such as `"a==b"` and `"a!=b"`, return `True` if variables can be assigned values so every equation is satisfied; otherwise return `False`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `equations`: list; equation strings.

**Output**
- `True` or `False`.

## Constraints
- `1 <= len(equations) <= 500`, each equation has length `4`

## Examples
```text
Input: equations = ["a==b","b!=c","c==a"]
Output: False
Explanation: Equality makes a, b, and c identical, contradicting b != c. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Equality is transitive, so equal variables form connected components. Inequality is valid only across different components. Union-find models this exactly.

## Approach 1 — Naive / Brute Force
**Idea:** Build transitive equality reachability with Floyd-Warshall, then test inequalities.
```python
class Solution:
    def equationsPossible(self, equations: list[str]) -> bool:
        same = [[False] * 26 for _ in range(26)]
        for i in range(26):
            same[i][i] = True
        for eq in equations:
            if eq[1:3] == "==":
                a, b = ord(eq[0]) - 97, ord(eq[3]) - 97
                same[a][b] = same[b][a] = True
        for k in range(26):
            for i in range(26):
                for j in range(26):
                    same[i][j] = same[i][j] or (same[i][k] and same[k][j])
        for eq in equations:
            if eq[1:3] == "!=":
                a, b = ord(eq[0]) - 97, ord(eq[3]) - 97
                if same[a][b]:
                    return False
        return True
```
- **Time:** O(26^3 + m) — **Space:** O(26^2)

## Approach 2 — Better
**Idea:** DFS equality components from an adjacency list and compare component ids.
```python
class Solution:
    def equationsPossible(self, equations: list[str]) -> bool:
        g = [[] for _ in range(26)]
        for eq in equations:
            if eq[1:3] == "==":
                a, b = ord(eq[0]) - 97, ord(eq[3]) - 97
                g[a].append(b); g[b].append(a)
        comp = [-1] * 26; cid = 0
        for i in range(26):
            if comp[i] == -1:
                stack = [i]; comp[i] = cid
                while stack:
                    u = stack.pop()
                    for v in g[u]:
                        if comp[v] == -1:
                            comp[v] = cid; stack.append(v)
                cid += 1
        for eq in equations:
            if eq[1:3] == "!=":
                a, b = ord(eq[0]) - 97, ord(eq[3]) - 97
                if comp[a] == comp[b]:
                    return False
        return True
```
- **Time:** O(m + 26) — **Space:** O(m + 26)

## Approach 3 — Optimal
**Idea:** Union all equal pairs first, then reject any inequality inside one set.
```python
class Solution:
    def equationsPossible(self, equations: list[str]) -> bool:
        parent = list(range(26))
        rank = [0] * 26
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        def union(a, b):
            ra, rb = find(a), find(b)
            if ra == rb:
                return
            if rank[ra] < rank[rb]:
                ra, rb = rb, ra
            parent[rb] = ra
            if rank[ra] == rank[rb]:
                rank[ra] += 1
        for eq in equations:
            if eq[1:3] == "==":
                union(ord(eq[0]) - 97, ord(eq[3]) - 97)
        for eq in equations:
            if eq[1:3] == "!=" and find(ord(eq[0]) - 97) == find(ord(eq[3]) - 97):
                return False
        return True
```
- **Time:** O(m α(26)) — **Space:** O(26)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(26^3 + m) | O(26^2) |
| Better | O(m + 26) | O(m + 26) |
| Optimal | O(m α(26)) | O(26) |

## Edge Cases & Pitfalls
- Process all equalities before inequalities.
- `a!=a` is impossible.

## Related
- Union-Find
- Connected Components
