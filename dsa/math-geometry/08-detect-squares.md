# 08. Detect Squares

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Google, Meta, Amazon

## Problem
Design a data structure that stores points on a 2-D integer grid and answers square-count queries.

`add(point)` inserts one occurrence of `point = [x, y]`. Duplicate points are allowed and each occurrence counts separately. `count(point)` returns how many ways the query point can be combined with three previously added points to form an **axis-aligned square** with positive side length. The query point does not need to have been added before.

**Input**
- A sequence of operations on `DetectSquares`:
  - `DetectSquares()`: create an empty structure.
  - `add(point)`: add one occurrence of `[x, y]`.
  - `count(point)`: count axis-aligned squares using `[x, y]` as one corner.

**Output**
- For each `count` operation, return the number of valid squares for that query. Constructor and `add` operations return no value.

## Constraints
- `point.length == 2`
- `0 <= x, y <= 1000`
- At most `3000` calls are made in total to `add` and `count`.
- Duplicate added points are valid and multiply the number of square choices.

## Examples
```text
Input: add([3,10]), add([11,2]), add([3,2]), count([11,10])
Output: 1
Explanation: The query point `[11,10]` and the added points `[3,10]`, `[11,2]`, and `[3,2]` are the four corners of one axis-aligned square of side length `8`.
```

```text
Input: add([0,0]), count([1,1])
Output: 0
Explanation: Only one point has been added, so there are not enough corners to form a square.
```

## Understanding & Intuition
An axis-aligned square is determined by a query point and one diagonal point with different x and y offsets of equal length. Then the other two corners must exist. Duplicate points multiply the count.

## Approach 1 — Naive / Brute Force
**Idea:** Store every point occurrence and test all triples with the query.
```python
from typing import List

class DetectSquares:
    def __init__(self):
        self.points = []

    def add(self, point: List[int]) -> None:
        self.points.append(tuple(point))

    def count(self, point: List[int]) -> int:
        qx, qy = point
        ans = 0
        pts = self.points
        for dx, dy in pts:
            if abs(dx - qx) != abs(dy - qy) or dx == qx:
                continue
            corner1 = corner2 = 0
            for px, py in pts:
                if (px, py) == (qx, dy):
                    corner1 += 1
                if (px, py) == (dx, qy):
                    corner2 += 1
            # Each diagonal occurrence pairs with every duplicate corner choice.
            ans += corner1 * corner2
        return ans
```
- **Time:** O(p²) per count — **Space:** O(p)

## Approach 2 — Better
**Idea:** Count point frequencies and enumerate diagonal candidates.
```python
from collections import Counter
from typing import List, Tuple

class DetectSquares:
    def __init__(self):
        self.freq = Counter()

    def add(self, point: List[int]) -> None:
        self.freq[tuple(point)] += 1

    def count(self, point: List[int]) -> int:
        x, y = point
        ans = 0
        for (dx, dy), diagonal_count in self.freq.items():
            if abs(dx - x) == abs(dy - y) and dx != x:
                ans += diagonal_count * self.freq[(x, dy)] * self.freq[(dx, y)]
        return ans
```
- **Time:** O(u) per count — **Space:** O(u)

## Approach 3 — Optimal
**Idea:** Group counts by x-coordinate and enumerate only points sharing the query x.
```python
from collections import Counter, defaultdict
from typing import List

class DetectSquares:
    def __init__(self):
        self.points = Counter()
        self.by_x = defaultdict(Counter)

    def add(self, point: List[int]) -> None:
        x, y = point
        self.points[(x, y)] += 1
        self.by_x[x][y] += 1

    def count(self, point: List[int]) -> int:
        x, y = point
        ans = 0
        for other_y, vertical_count in self.by_x[x].items():
            side = other_y - y
            if side == 0:
                continue
            # Try squares extending left and right by the side length.
            for nx in (x + side, x - side):
                ans += vertical_count * self.points[(nx, y)] * self.points[(nx, other_y)]
        return ans
```
- **Time:** O(k) per count, where k is points with query x — **Space:** O(u)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(p²) per count | O(p) |
| Better | O(u) per count | O(u) |
| Optimal | O(k) per count | O(u) |

## Edge Cases & Pitfalls
- Duplicate points multiply the answer.
- Ignore zero side length.

## Related
- Number of Boomerangs
- Valid Square
