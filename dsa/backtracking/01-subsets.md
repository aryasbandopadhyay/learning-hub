# 01. Subsets

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer array `nums` with distinct elements, return all possible subsets (the power set). `1 <= len(nums) <= 10`; values are distinct. The answer may be returned in any order.

## Examples
```text
Input: nums = [1,2,3]
Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
Explanation: Every element is either chosen or skipped.
```

## Understanding & Intuition
Each number has two states: included or excluded. That naturally forms a binary decision tree. Backtracking records the current path whenever it represents a valid subset.

## Approach 1 — Naive / Brute Force
**Idea:** Try every bitmask from `0` to `2^n - 1` and build the subset represented by set bits.
```python
from typing import List

class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        result = []
        n = len(nums)
        for mask in range(1 << n):
            subset = []
            for i in range(n):
                if mask & (1 << i):
                    subset.append(nums[i])
            result.append(subset)
        return result
```
- **Time:** O(n * 2^n) — **Space:** O(n * 2^n)

## Approach 2 — Better
**Idea:** Use include/exclude recursion; at each index decide whether to take the number.
```python
from typing import List

class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        result = []
        path = []

        def dfs(i: int) -> None:
            if i == len(nums):
                result.append(path.copy())
                return
            dfs(i + 1)              # Skip nums[i].
            path.append(nums[i])
            dfs(i + 1)              # Take nums[i].
            path.pop()

        dfs(0)
        return result
```
- **Time:** O(n * 2^n) — **Space:** O(n * 2^n)

## Approach 3 — Optimal
**Idea:** Grow subsets by start index, appending each current path once and only exploring later elements.
```python
from typing import List

class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        result = []
        path = []

        def backtrack(start: int) -> None:
            result.append(path.copy())
            for i in range(start, len(nums)):
                path.append(nums[i])
                backtrack(i + 1)
                path.pop()

        backtrack(0)
        return result
```
- **Time:** O(n * 2^n) — **Space:** O(n * 2^n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * 2^n) | O(n * 2^n) |
| Better | O(n * 2^n) | O(n * 2^n) |
| Optimal | O(n * 2^n) | O(n * 2^n) |

## Edge Cases & Pitfalls
- Include the empty subset.
- Copy `path`; appending the same list reference corrupts results.
- Output order is not important.

## Related
- Subsets II
- Combinations
- Permutations
