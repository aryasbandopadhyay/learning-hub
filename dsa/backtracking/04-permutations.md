# 04. Permutations

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given an array `nums` of distinct integers, return all possible permutations that use every element
exactly once.

**Input**
- `nums`: a list of distinct integers.

**Output**
- A list of permutations. The judge accepts the permutations in any order.

## Constraints
- 1 <= nums.length <= 6
- -10 <= nums[i] <= 10
- All values in `nums` are unique.

## Examples
```text
Input: nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
Explanation: There are `3! = 6` ways to arrange the three distinct numbers, and all six permutations are shown.
```

## Understanding & Intuition
Permutation generation fills one position at a time. Once a number is used, it cannot be reused in the same path. Backtracking undoes each choice so another number can occupy the position.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively build all length-`n` sequences and reject sequences that reuse a value.
```python
from typing import List

class Solution:
    def permute(self, nums: List[int]) -> List[List[int]]:
        result = []
        path = []

        def dfs() -> None:
            if len(path) == len(nums):
                if len(set(path)) == len(nums):
                    result.append(path.copy())
                return
            for value in nums:
                path.append(value)
                dfs()
                path.pop()

        dfs()
        return result
```
- **Time:** O(n^n * n) — **Space:** O(n) auxiliary plus output

## Approach 2 — Better
**Idea:** Track used indices to avoid invalid repeated choices during recursion.
```python
from typing import List

class Solution:
    def permute(self, nums: List[int]) -> List[List[int]]:
        result = []
        path = []
        used = [False] * len(nums)

        def backtrack() -> None:
            if len(path) == len(nums):
                result.append(path.copy())
                return
            for i, value in enumerate(nums):
                if used[i]:
                    continue
                used[i] = True
                path.append(value)
                backtrack()
                path.pop()
                used[i] = False

        backtrack()
        return result
```
- **Time:** O(n * n!) — **Space:** O(n) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Swap each candidate into the current position in-place, reducing extra tracking state.
```python
from typing import List

class Solution:
    def permute(self, nums: List[int]) -> List[List[int]]:
        result = []

        def backtrack(first: int) -> None:
            if first == len(nums):
                result.append(nums.copy())
                return
            for i in range(first, len(nums)):
                nums[first], nums[i] = nums[i], nums[first]
                backtrack(first + 1)
                nums[first], nums[i] = nums[i], nums[first]

        backtrack(0)
        return result
```
- **Time:** O(n * n!) — **Space:** O(n) recursion plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^n * n) | O(n) plus output |
| Better | O(n * n!) | O(n) plus output |
| Optimal | O(n * n!) | O(n) plus output |

## Edge Cases & Pitfalls
- Append a copy, not the mutable working list.
- Distinct input means no duplicate-skip logic is needed.
- Restore swaps after recursion.

## Related
- Permutations II
- Subsets
- Combinations
