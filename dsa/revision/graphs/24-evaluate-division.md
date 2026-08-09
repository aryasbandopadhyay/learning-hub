# 24. Evaluate Division

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Uber

## Problem
You are given equations of the form `A / B = value`. Use them to answer division queries.

Each variable is a string. Equations can be combined transitively: if `a / b = 2` and `b / c = 3`, then `a / c = 6`. For a query involving an unknown variable or a disconnected component, return `-1.0`.

**Input**
- `equations`: a list of `[numerator, denominator]` variable pairs.
- `values`: a list where `values[i]` is the quotient for `equations[i]`.
- `queries`: a list of `[numerator, denominator]` pairs to evaluate.

**Output**
- A list of floating-point answers in the exact same order as `queries`.

## Constraints
- `1 <= equations.length <= 20`
- `equations.length == values.length`
- `1 <= queries.length <= 20`
- Variable names contain lowercase letters and digits and have length at least `1`.
- `0.0 < values[i] <= 20.0`

## Examples
```text
Input: equations = [["a","b"],["b","c"]], values = [2.0,3.0], queries = [["a","c"],["c","a"],["x","x"]]
Output: [6.0,0.1666666667,-1.0]
Explanation: `a/c` is found by multiplying `a/b` and `b/c`, giving `6.0`. The reverse query `c/a` is its reciprocal, and `x/x` is unknown because `x` never appears in the equations.
```

```text
Input: equations = [["a","b"]], values = [2.0], queries = [["b","a"],["a","e"]]
Output: [0.5,-1.0]
Explanation: `b/a` is the reciprocal of `a/b`, while `e` is unknown.
```

## Understanding & Intuition
Equations form a weighted graph. Query answers are products along paths.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def calcEquation(self, equations: List[List[str]], values: List[float], queries: List[List[str]]) -> List[float]:
        # Weighted union-find stores ratio to root.
        parent, weight = {}, {}
        def find(x):
            if x not in parent:
                parent[x], weight[x] = x, 1.0
            if parent[x] != x:
                root = find(parent[x])
                weight[x] *= weight[parent[x]]
                parent[x] = root
            return parent[x]
        def union(a, b, val):
            ra, rb = find(a), find(b)
            if ra != rb:
                parent[ra] = rb
                weight[ra] = val * weight[b] / weight[a]
        for (a,b), val in zip(equations, values):
            union(a, b, val)
        ans = []
        for a, b in queries:
            if a not in parent or b not in parent or find(a) != find(b):
                ans.append(-1.0)
            else:
                ans.append(weight[a] / weight[b])
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def calcEquation(self, equations: List[List[str]], values: List[float], queries: List[List[str]]) -> List[float]:
        # Weighted union-find stores ratio to root.
        parent, weight = {}, {}
        def find(x):
            if x not in parent:
                parent[x], weight[x] = x, 1.0
            if parent[x] != x:
                root = find(parent[x])
                weight[x] *= weight[parent[x]]
                parent[x] = root
            return parent[x]
        def union(a, b, val):
            ra, rb = find(a), find(b)
            if ra != rb:
                parent[ra] = rb
                weight[ra] = val * weight[b] / weight[a]
        for (a,b), val in zip(equations, values):
            union(a, b, val)
        ans = []
        for a, b in queries:
            if a not in parent or b not in parent or find(a) != find(b):
                ans.append(-1.0)
            else:
                ans.append(weight[a] / weight[b])
        return ans
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def calcEquation(self, equations: List[List[str]], values: List[float], queries: List[List[str]]) -> List[float]:
        # Weighted union-find stores ratio to root.
        parent, weight = {}, {}
        def find(x):
            if x not in parent:
                parent[x], weight[x] = x, 1.0
            if parent[x] != x:
                root = find(parent[x])
                weight[x] *= weight[parent[x]]
                parent[x] = root
            return parent[x]
        def union(a, b, val):
            ra, rb = find(a), find(b)
            if ra != rb:
                parent[ra] = rb
                weight[ra] = val * weight[b] / weight[a]
        for (a,b), val in zip(equations, values):
            union(a, b, val)
        ans = []
        for a, b in queries:
            if a not in parent or b not in parent or find(a) != find(b):
                ans.append(-1.0)
            else:
                ans.append(weight[a] / weight[b])
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
