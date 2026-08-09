# 04. Triangle Minimum Path Sum

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
You are given a triangle of numbers, where row `r` contains `r + 1` values.

Start at the top. From index `c` in a row, move to index `c` or `c + 1` in the next row. Return the minimum possible sum from the top to any value in the bottom row.

**Input**
- `triangle`: a list of rows forming a numeric triangle.

**Output**
- The minimum top-to-bottom path sum.

## Constraints
- `1 <= triangle.length <= 200`
- `triangle[r].length == r + 1`
- `-10^4 <= triangle[r][c] <= 10^4`

## Examples
```text
Input: triangle = [[2],[3,4],[6,5,7],[4,1,8,3]]
Output: 11
Explanation: The path `2 -> 3 -> 5 -> 1` sums to `11`, which is the smallest valid adjacent downward path.
```

## Understanding & Intuition
Each entry has at most two parents above it. A bottom-up view lets every parent choose the cheaper of its two children. This compresses naturally to one row.

## Approach 1 — Naive / Brute Force
**Idea:** Fill a full triangular DP table.
```python
class Solution:
    def minimumTotal(self, triangle: list[list[int]]) -> int:
        dp = [row[:] for row in triangle]
        for r in range(1, len(triangle)):
            for c in range(len(triangle[r])):
                best = 10 ** 18
                if c < len(triangle[r - 1]):
                    best = min(best, dp[r - 1][c])
                if c:
                    best = min(best, dp[r - 1][c - 1])
                dp[r][c] += best
        return min(dp[-1])
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Update one row from right to left while moving downward.
```python
class Solution:
    def minimumTotal(self, triangle: list[list[int]]) -> int:
        dp = [10 ** 18] * (len(triangle) + 1)
        dp[0] = 0
        for row in triangle:
            for c in range(len(row) - 1, -1, -1):
                dp[c] = min(dp[c], dp[c - 1] if c else 10 ** 18) + row[c]
        return min(dp[:len(triangle)])
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Start from the last row and collapse upward.
```python
class Solution:
    def minimumTotal(self, triangle: list[list[int]]) -> int:
        dp = triangle[-1][:]
        for r in range(len(triangle) - 2, -1, -1):
            for c in range(len(triangle[r])):
                dp[c] = triangle[r][c] + min(dp[c], dp[c + 1])
        return dp[0]
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n^2) | O(n) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Negative values make greedy choices unsafe.
- Edge entries have one parent.
- The triangle is not rectangular.

## Related
- Minimum Falling Path Sum
- Minimum Path Sum
