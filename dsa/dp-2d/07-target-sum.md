# 07. Target Sum

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given integers `nums` and a target value, place either a `+` or `-` sign before every number. Count
how many sign assignments make the resulting expression equal `target`.

**Input**
- `nums`: a list of non-negative integers.
- `target`: the desired expression value.

**Output**
- An integer: the number of valid sign assignments.

## Constraints
- 1 <= nums.length <= 20
- 0 <= nums[i] <= 1000
- 0 <= sum(nums) <= 1000
- -1000 <= target <= 1000

## Examples
```text
Input: nums = [1,1,1,1,1], target = 3
Output: 5
Explanation: There are five ways to make `3`, each using one of the five `1`s as the negative term and the rest as positive terms.
```

## Understanding & Intuition
Let `dp[i][sum]` be the number of ways after processing index `i` with current sum `sum`. Each number branches into adding or subtracting it. Memoization or tabulation collapses repeated `(index, sum)` states.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def findTargetSumWays(self, nums: List[int], target: int) -> int:
        def dfs(i: int, total: int) -> int:
            if i == len(nums):
                return 1 if total == target else 0
            return dfs(i + 1, total + nums[i]) + dfs(i + 1, total - nums[i])

        return dfs(0, 0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def findTargetSumWays(self, nums: List[int], target: int) -> int:
        memo = {}

        def dfs(i: int, total: int) -> int:
            if i == len(nums):
                return 1 if total == target else 0
            if (i, total) not in memo:
                memo[(i, total)] = dfs(i + 1, total + nums[i]) + dfs(i + 1, total - nums[i])
            return memo[(i, total)]

        return dfs(0, 0)
```
- **Time:** O(n*S) — **Space:** O(n*S)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def findTargetSumWays(self, nums: List[int], target: int) -> int:
        counts = {0: 1}
        for num in nums:
            nxt = {}
            for total, ways in counts.items():
                nxt[total + num] = nxt.get(total + num, 0) + ways
                nxt[total - num] = nxt.get(total - num, 0) + ways
            counts = nxt
        return counts.get(target, 0)
```
- **Time:** O(n*S) — **Space:** O(S)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n*S) | O(n*S) |
| Optimal | O(n*S) | O(S) |

## Edge Cases & Pitfalls
- Zeros double the number of ways because `+0` and `-0` are distinct assignments.
- `S` is the range of reachable sums, at most twice the sum of `nums`.

## Related
- 0/1 Knapsack
- Partition Equal Subset Sum
