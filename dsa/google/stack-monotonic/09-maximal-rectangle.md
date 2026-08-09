# 09. Maximal Rectangle

- **Difficulty:** Hard
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
Given a binary matrix of `0`s and `1`s, return the area of the largest rectangle containing only `1`s.

Implement `Solution.maximalRectangle` with the parameters below and return the requested value.

**Input**
- `matrix`: a `list[list[int]]`; the binary matrix.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(matrix), len(matrix[0]) <= 200`
- every cell is `0` or `1`

## Examples
```text
Input: matrix = [[1,0,1,0,0],[1,0,1,1,1],[1,1,1,1,1],[1,0,0,1,0]]
Output: 6
Explanation: The largest all-ones rectangle has height 2 and width 3.
```

## Understanding & Intuition
Each row can be treated as the base of a histogram whose heights count consecutive ones above it. Brute force can test every rectangle with prefix sums. The optimal solution applies the largest-rectangle-in-histogram stack routine to each row's heights.

## Approach 1 — Naive / Brute Force
**Idea:** Use a 2D prefix sum and test every possible rectangle.
```python
from typing import List

class Solution:
    def maximalRectangle(self, matrix: List[List[int]]) -> int:
        if not matrix or not matrix[0]:
            return 0
        rows, cols = len(matrix), len(matrix[0])
        pref = [[0] * (cols + 1) for _ in range(rows + 1)]
        for r in range(rows):
            for c in range(cols):
                pref[r + 1][c + 1] = matrix[r][c] + pref[r][c + 1] + pref[r + 1][c] - pref[r][c]
        best = 0
        for r1 in range(rows):
            for r2 in range(r1, rows):
                for c1 in range(cols):
                    for c2 in range(c1, cols):
                        area = (r2 - r1 + 1) * (c2 - c1 + 1)
                        total = pref[r2 + 1][c2 + 1] - pref[r1][c2 + 1] - pref[r2 + 1][c1] + pref[r1][c1]
                        if total == area:
                            best = max(best, area)
        return best
```
- **Time:** O(m^2 n^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Build histogram heights for each row and test every width with the minimum height.
```python
from typing import List

class Solution:
    def maximalRectangle(self, matrix: List[List[int]]) -> int:
        if not matrix or not matrix[0]:
            return 0
        cols = len(matrix[0])
        heights = [0] * cols
        best = 0
        for row in matrix:
            for c, value in enumerate(row):
                heights[c] = heights[c] + 1 if value == 1 else 0
            for left in range(cols):
                min_height = float('inf')
                for right in range(left, cols):
                    min_height = min(min_height, heights[right])
                    best = max(best, min_height * (right - left + 1))
        return best
```
- **Time:** O(m n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** For each row histogram, use an increasing stack to find each bar's maximal width.
```python
from typing import List

class Solution:
    def maximalRectangle(self, matrix: List[List[int]]) -> int:
        if not matrix or not matrix[0]:
            return 0
        def largest_histogram(heights: List[int]) -> int:
            stack = []
            best = 0
            for i, height in enumerate(heights + [0]):
                while stack and heights[stack[-1]] > height:
                    h = heights[stack.pop()]
                    left = stack[-1] if stack else -1
                    best = max(best, h * (i - left - 1))
                stack.append(i)
            return best
        heights = [0] * len(matrix[0])
        best = 0
        for row in matrix:
            for c, value in enumerate(row):
                heights[c] = heights[c] + 1 if value == 1 else 0
            best = max(best, largest_histogram(heights))
        return best
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m^2 n^2) | O(mn) |
| Better | O(m n^2) | O(n) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Rows containing all zeros contribute area `0`.
- Use integer `0`/`1` cells for JSON-friendly input.
- Append a sentinel height to flush the histogram stack.

## Related
- Maximal Square
- Largest Rectangle in Histogram
