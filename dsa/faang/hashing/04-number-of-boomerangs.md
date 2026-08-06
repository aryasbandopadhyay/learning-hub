# 04. Number of Boomerangs

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Google, Meta, Uber

## Problem
Given `points`, where each point is `[x, y]`, return the number of boomerangs. A boomerang is an ordered tuple of distinct points `(i, j, k)` such that the distance between `i` and `j` equals the distance between `i` and `k`.

Constraints: `1 <= len(points) <= 500`, coordinates are integers, and duplicate point coordinates may appear but each list entry is a distinct point.

## Examples
```text
Input: points = [[0, 0], [1, 0], [2, 0]]
Output: 2
Explanation: Centering at [1,0], the two endpoints can be ordered in 2 ways.
```

## Understanding & Intuition
For a fixed center point, only the squared distance to every other point matters. If `m` points share a distance from the center, they create `m * (m - 1)` ordered endpoint pairs. Summing this over every center counts all boomerangs.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every ordered triple of distinct indices and compare squared distances.
```python
class Solution:
    def numberOfBoomerangs(self, points: list[list[int]]) -> int:
        def dist2(a, b):
            dx = points[a][0] - points[b][0]
            dy = points[a][1] - points[b][1]
            return dx * dx + dy * dy

        n = len(points)
        ans = 0
        for i in range(n):
            for j in range(n):
                if i == j:
                    continue
                for k in range(n):
                    if i != k and j != k and dist2(i, j) == dist2(i, k):
                        ans += 1
        return ans
```
- **Time:** O(n³) — **Space:** O(1)

## Approach 2 — Better
**Idea:** For each center, sort all squared distances and count equal runs.
```python
class Solution:
    def numberOfBoomerangs(self, points):
        n = len(points)
        ans = 0
        for i in range(n):
            dists = []
            x1, y1 = points[i]
            for j in range(n):
                if i != j:
                    x2, y2 = points[j]
                    dists.append((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
            dists.sort()
            run = 1
            for p in range(1, len(dists) + 1):
                if p < len(dists) and dists[p] == dists[p - 1]:
                    run += 1
                else:
                    ans += run * (run - 1)
                    run = 1
        return ans
```
- **Time:** O(n² log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** For each center, count squared distances in a hashmap and add ordered pairs as counts grow.
```python
class Solution:
    def numberOfBoomerangs(self, points):
        ans = 0
        for x1, y1 in points:
            counts = {}
            for x2, y2 in points:
                d = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
                ans += 2 * counts.get(d, 0)
                counts[d] = counts.get(d, 0) + 1
        return ans
```
- **Time:** O(n²) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n³) | O(1) |
| Better | O(n² log n) | O(n) |
| Optimal | O(n²) | O(n) |

## Edge Cases & Pitfalls
- Use squared distance to avoid floating-point precision.
- Boomerangs are ordered, so multiply combinations by 2.
- Duplicate coordinates are still distinct points and can create distance zero.

## Related
- Detect Squares
- Max Points on a Line
