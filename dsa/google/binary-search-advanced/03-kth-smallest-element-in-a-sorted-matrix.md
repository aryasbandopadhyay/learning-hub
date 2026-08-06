# 03. Kth Smallest Element in a Sorted Matrix

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given an `n x n` matrix where each row and each column is sorted in nondecreasing order, return the `k`th smallest element in the matrix.

Constraints: `1 <= n <= 300`, `1 <= k <= n*n`, `-10^9 <= matrix[i][j] <= 10^9`.

## Examples
```text
Input: matrix = [[1,5,9],[10,11,13],[12,13,15]], k = 8
Output: 13
Explanation: The sorted order is [1,5,9,10,11,12,13,13,15], so the 8th value is 13.
```

## Understanding & Intuition
The answer is a value, not necessarily a position. For any guessed value `x`, we can count how many matrix values are `<= x`; this count is monotonic, enabling binary search on the value range.

## Approach 1 — Naive / Brute Force
**Idea:** Flatten the matrix, sort all values, and index the `k`th element.
```python
class Solution:
    def kthSmallest(self, matrix: list[list[int]], k: int) -> int:
        vals = []
        for row in matrix:
            for x in row:
                vals.append(x)
        vals.sort()
        return vals[k - 1]
```
- **Time:** O(n^2 log n) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Use a min-heap seeded with the first value of each row and pop `k` times.
```python
class Solution:
    def kthSmallest(self, matrix: list[list[int]], k: int) -> int:
        import heapq
        n = len(matrix)
        heap = []
        for r in range(n):
            heapq.heappush(heap, (matrix[r][0], r, 0))
        ans = 0
        for _ in range(k):
            ans, r, c = heapq.heappop(heap)
            if c + 1 < n:
                heapq.heappush(heap, (matrix[r][c + 1], r, c + 1))
        return ans
```
- **Time:** O(k log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Binary search values and count `<= mid` from the bottom-left corner in O(n).
```python
class Solution:
    def kthSmallest(self, matrix: list[list[int]], k: int) -> int:
        n = len(matrix)
        lo, hi = matrix[0][0], matrix[-1][-1]
        while lo < hi:
            mid = (lo + hi) // 2
            count = 0
            r, c = n - 1, 0
            while r >= 0 and c < n:
                if matrix[r][c] <= mid:
                    count += r + 1
                    c += 1
                else:
                    r -= 1
            if count >= k:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log R) — **Space:** O(1), where `R` is the value range

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n^2) |
| Better | O(k log n) | O(n) |
| Optimal | O(n log R) | O(1) |

## Edge Cases & Pitfalls
- Duplicate values count separately.
- `k = 1` and `k = n*n` return matrix corners.
- Binary search must be on values, not indexes.

## Related
- Median of Two Sorted Arrays
- Kth Smallest Pair Distance
