# 07. Search a 2D Matrix

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Bloomberg

## Problem
You are given an `m x n` matrix where each row is sorted and the first value of each row is greater than the last value of the previous row.

Return whether `target` appears in the matrix.

**Input**
- `matrix`: a globally sorted 2-D matrix.
- `target`: the value to find.

**Output**
- `true` if `target` is present; otherwise `false`.

## Constraints
- `m == matrix.length`
- `n == matrix[r].length`
- `1 <= m, n <= 100`
- `-10^4 <= matrix[r][c], target <= 10^4`.

## Examples
```text
Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
Output: true
Explanation: The matrix can be viewed as one sorted sequence. The value 3 appears in the first row, so the result is `true`.
```

## Understanding & Intuition
The row ordering makes the entire matrix equivalent to one sorted array. We can search linearly, row by row, or binary search. Mapping a 1D index back to row and column gives the optimal approach.

## Approach 1 — Naive / Brute Force
**Idea:** Check every cell until the target is found.
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
**Idea:** Binary search the candidate row, then binary search within that row.
```python
from bisect import bisect_right
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        first_values = [row[0] for row in matrix]
        row = bisect_right(first_values, target) - 1
        if row < 0:
            return False

        nums = matrix[row]
        left, right = 0, len(nums) - 1
        while left <= right:
            mid = (left + right) // 2
            if nums[mid] == target:
                return True
            if nums[mid] < target:
                left = mid + 1
            else:
                right = mid - 1
        return False
```
- **Time:** O(m + log n) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Binary search the virtual flattened sorted array.
```python
from typing import List

class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        left, right = 0, m * n - 1
        while left <= right:
            mid = (left + right) // 2
            r, c = divmod(mid, n)
            value = matrix[r][c]
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
| Better | O(m + log n) | O(m) |
| Optimal | O(log(mn)) | O(1) |

## Edge Cases & Pitfalls
- If `target` is smaller than the first element, there is no candidate row.
- Use `divmod(mid, n)`, not `m`, to map flattened index.

## Related
- Search a 2D Matrix II
- Binary Search
