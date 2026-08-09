# 04. K Closest Points to Origin

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft, Apple

## Problem
Given points in the plane, return the `k` points closest to `(0,0)`. Compare distance by squared Euclidean distance `x^2 + y^2`. If points tie at the boundary, any valid set is acceptable.

**Input**
- `points`: list of `[x, y]` coordinates.
- `k`: number of points to return.

**Output**
- Any list containing exactly the `k` closest points. The judge accepts any order for this problem.

## Constraints
- `1 <= k <= points.length <= 10^4`
- `points[i].length == 2`
- `-10^4 <= x, y <= 10^4`

## Examples
```text
Input: points = [[1,3],[-2,2]], k = 1
Output: [[-2,2]]
Explanation: `[-2,2]` has squared distance `8`, which is smaller than `[1,3]` with squared distance `10`.
```

## Understanding & Intuition
Because square root preserves order, compare squared distances. Sorting all points is easy but unnecessary if `k` is small. A bounded max-heap keeps only the closest `k`, while quickselect can partition the list in expected linear time.

## Approach 1 — Naive / Brute Force
**Idea:** Sort all points by squared distance and take the first `k`.
```python
from typing import List

class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        points.sort(key=lambda p: p[0] * p[0] + p[1] * p[1])
        return points[:k]
```
- **Time:** O(n log n) — **Space:** O(1) extra

## Approach 2 — Better
**Idea:** Keep a max-heap of size `k` by pushing negative squared distances.
```python
from typing import List
import heapq

class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        heap = []
        for x, y in points:
            dist = x * x + y * y
            heapq.heappush(heap, (-dist, x, y))
            # The farthest among kept points is removed.
            if len(heap) > k:
                heapq.heappop(heap)
        return [[x, y] for _, x, y in heap]
```
- **Time:** O(n log k) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Quickselect so the first `k` positions contain the `k` closest points, unordered.
```python
from typing import List
import random

class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        def dist(i: int) -> int:
            x, y = points[i]
            return x * x + y * y

        left, right = 0, len(points) - 1
        target = k - 1

        while left <= right:
            pivot_index = random.randint(left, right)
            pivot_dist = dist(pivot_index)
            points[pivot_index], points[right] = points[right], points[pivot_index]
            store = left

            for i in range(left, right):
                if dist(i) <= pivot_dist:
                    points[store], points[i] = points[i], points[store]
                    store += 1

            points[store], points[right] = points[right], points[store]
            if store == target:
                break
            if store < target:
                left = store + 1
            else:
                right = store - 1

        return points[:k]
```
- **Time:** O(n) average, O(n^2) worst — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(1) extra |
| Better | O(n log k) | O(k) |
| Optimal | O(n) average | O(1) |

## Edge Cases & Pitfalls
- Return order does not matter.
- Avoid computing square roots.
- Quickselect mutates the input points.

## Related
- Kth Largest Element in an Array
- Top K Frequent Elements
- Find K Pairs with Smallest Sums
