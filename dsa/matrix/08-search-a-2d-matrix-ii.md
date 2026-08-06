# 08. Search a 2D Matrix II

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
Given an `m x n` matrix where each row is sorted left-to-right and each column is sorted top-to-bottom, return whether `target` exists. Constraints: `1 <= m,n <= 300`.

## Examples
```text
Input: matrix = [[1,4,7,11,15],[2,5,8,12,19],[3,6,9,16,22],[10,13,14,17,24],[18,21,23,26,30]], target = 5
Output: true
Explanation: 5 is present at row 1 column 1.
```

## Understanding & Intuition
Rows and columns are independently sorted, but the matrix is not globally sorted as one array. A normal flattened binary search does not work. Starting from a corner lets each comparison eliminate one row or one column.

## Approach 1 — Naive / Brute Force
**Idea:** Scan every element.
```python
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        return any(value == target for row in matrix for value in row)
```
- **Time:** O(mn) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Binary search each row, skipping rows whose range cannot contain the target.
```python
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        for row in matrix:
            if row[0] <= target <= row[-1]:
                left, right = 0, len(row) - 1
                while left <= right:
                    mid = (left + right) // 2
                    if row[mid] == target:
                        return True
                    if row[mid] < target:
                        left = mid + 1
                    else:
                        right = mid - 1
        return False
```
- **Time:** O(m log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Start at the top-right; move left if too large, down if too small.
```python
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        r, c = 0, n - 1
        while r < m and c >= 0:
            value = matrix[r][c]
            if value == target:
                return True
            if value > target:
                c -= 1  # Current column below is even larger.
            else:
                r += 1  # Current row to the left is even smaller.
        return False
```
- **Time:** O(m+n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(1) |
| Better | O(m log n) | O(1) |
| Optimal | O(m+n) | O(1) |

## Edge Cases & Pitfalls
- Do not flatten and binary search; global sorted order is not guaranteed.
- Top-right and bottom-left corners both work; top-left does not eliminate cleanly.

## Related
- Search a 2D Matrix
- Kth Smallest Element in a Sorted Matrix
