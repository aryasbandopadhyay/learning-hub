# 07. Diagonal Traverse II

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Google, Meta, Amazon

## Problem
Given a possibly jagged list `nums`, return all values in diagonal order: increasing `r + c`, and within each diagonal from larger row index to smaller row index.

Constraints: `1 <= total number of values <= 10^5`.

## Examples
```text
Input: nums = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,4,2,7,5,3,8,6,9]
Explanation: Diagonals are grouped by r+c and read from bottom to top.
```

## Understanding & Intuition
For jagged rows, rectangular indexing assumptions can fail. The diagonal key is still `r + c`, and the required tie-break is descending row. Grouping or sorting by these two facts gives the canonical order.

## Approach 1 — Naive / Brute Force
**Idea:** Create `(diagonal, -row, value)` triples for all values and sort them.
```python
class Solution:
    def findDiagonalOrder(self, nums: list[list[int]]) -> list[int]:
        items = []
        for r, row in enumerate(nums):
            for c, val in enumerate(row):
                items.append((r + c, -r, val))
        items.sort()
        return [val for _, _, val in items]
```
- **Time:** O(N log N) — **Space:** O(N)

## Approach 2 — Better
**Idea:** Group values by `r+c` while scanning rows top-down, then reverse each group.
```python
class Solution:
    def findDiagonalOrder(self, nums: list[list[int]]) -> list[int]:
        groups = {}
        max_key = 0
        for r, row in enumerate(nums):
            for c, val in enumerate(row):
                key = r + c
                groups.setdefault(key, []).append(val)
                max_key = max(max_key, key)
        ans = []
        for key in range(max_key + 1):
            if key in groups:
                ans.extend(reversed(groups[key]))
        return ans
```
- **Time:** O(N + D) — **Space:** O(N)

## Approach 3 — Optimal
**Idea:** Use the same grouping but append rows in reverse order, avoiding per-group reversal.
```python
class Solution:
    def findDiagonalOrder(self, nums: list[list[int]]) -> list[int]:
        groups = {}
        max_key = 0
        for r in range(len(nums) - 1, -1, -1):
            for c, val in enumerate(nums[r]):
                key = r + c
                groups.setdefault(key, []).append(val)
                max_key = max(max_key, key)
        ans = []
        for key in range(max_key + 1):
            ans.extend(groups.get(key, []))
        return ans
```
- **Time:** O(N + D) — **Space:** O(N)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(N log N) | O(N) |
| Better | O(N + D) | O(N) |
| Optimal | O(N + D) | O(N) |

## Edge Cases & Pitfalls
- Rows may have different lengths.
- Do not visit nonexistent jagged cells.
- Empty inner rows should simply contribute no values.

## Related
- Diagonal Traverse
- Sort the Matrix Diagonally
