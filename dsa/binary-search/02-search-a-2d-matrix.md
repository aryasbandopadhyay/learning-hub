# 02. Search a 2D Matrix

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given an `m x n` matrix where each row is sorted and the first integer of each row is greater than the last integer of the previous row, return whether `target` exists. Constraints: `1 <= m, n <= 100`, `-10^4 <= matrix[i][j], target <= 10^4`.

## Examples
```text
Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
Output: true
Explanation: 3 is present in the first row.
```

## Understanding & Intuition
The matrix behaves like one sorted array if rows are laid end to end. We can either scan rows, binary search a row, or binary search directly over virtual indices. Mapping index `mid` to `matrix[mid // n][mid % n]` gives O(1) access.

## Approach 1 — Naive / Brute Force
**Idea:** Check each cell.
```python
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        for row in matrix:
            for value in row:
                if value == target:
                    return True
        return False
```
- **Time:** O(mn) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Find the only possible row, then binary search inside it.
```python
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        for row in matrix:
            # Because rows are disjoint ranges, only this row can contain target.
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
        return False
```
- **Time:** O(m + log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search the matrix as one virtual sorted array.
```python
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        left, right = 0, m * n - 1
        while left <= right:
            mid = (left + right) // 2
            value = matrix[mid // n][mid % n]
            if value == target:
                return True
            if value < target:
                left = mid + 1
            else:
                right = mid - 1
        return False
```
- **Time:** O(log(mn)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(1) |
| Better | O(m + log n) | O(1) |
| Optimal | O(log(mn)) | O(1) |

## Edge Cases & Pitfalls
- The matrix is non-empty by constraint, but still use `len(matrix[0])` carefully.
- Do not binary search rows independently after the valid range is passed.
- Convert virtual indices with the column count, not the row count.

## Related
- Binary Search
- Search a 2D Matrix II

