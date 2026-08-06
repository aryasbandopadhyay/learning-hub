# 05. Line Reflection

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Google, LinkedIn, Meta

## Problem
Given a set of 2D integer points, determine whether there exists a vertical line `x = c` such that reflecting every point across the line produces another point in the set. Duplicate points do not change the answer.

Return `True` if such a line exists, otherwise return `False`.

Constraints: `1 <= len(points) <= 10^4`, each point is `[x, y]`, and coordinates fit in signed 32-bit integers.

## Examples
```text
Input: points = [[1, 1], [-1, 1], [1, -1], [-1, -1]]
Output: True
Explanation: The vertical line x = 0 reflects every point onto another point.
```

## Understanding & Intuition
A valid vertical reflection line must lie halfway between the minimum and maximum x-coordinates. For every unique point `(x, y)`, the reflected point has x-coordinate `min_x + max_x - x` and the same y. A hash set makes each membership test constant time.

## Approach 1 — Naive / Brute Force
**Idea:** Try every candidate mirror determined by a pair of x-values and test all points by linear search.
```python
class Solution:
    def isReflected(self, points: list[list[int]]) -> bool:
        unique = []
        for p in points:
            if p not in unique:
                unique.append(p)
        xs = [p[0] for p in unique]
        candidates = set()
        for a in xs:
            for b in xs:
                candidates.add(a + b)
        for total in candidates:
            ok = True
            for x, y in unique:
                if [total - x, y] not in unique:
                    ok = False
                    break
            if ok:
                return True
        return False
```
- **Time:** O(n³) — **Space:** O(n²)

## Approach 2 — Better
**Idea:** Sort unique points by y, then x, and compare mirrored pairs within each horizontal row.
```python
class Solution:
    def isReflected(self, points):
        unique = sorted({(x, y) for x, y in points}, key=lambda p: (p[1], p[0]))
        total = min(x for x, y in unique) + max(x for x, y in unique)
        rows = {}
        for x, y in unique:
            rows.setdefault(y, []).append(x)
        for xs in rows.values():
            i, j = 0, len(xs) - 1
            while i <= j:
                if xs[i] + xs[j] != total:
                    return False
                i += 1
                j -= 1
        return True
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Put unique points in a set and verify every reflected counterpart across `min_x + max_x`.
```python
class Solution:
    def isReflected(self, points):
        seen = {(x, y) for x, y in points}
        min_x = min(x for x, y in seen)
        max_x = max(x for x, y in seen)
        total = min_x + max_x
        for x, y in seen:
            if (total - x, y) not in seen:
                return False
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n³) | O(n²) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Duplicates should be ignored for reflection existence.
- Avoid floating-point `c`; compare doubled coordinates using `min_x + max_x`.
- Every y-level must reflect around the same vertical line.

## Related
- Valid Square
- Max Points on a Line
