# 08. Delete and Earn

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Facebook

## Problem
You are given an integer array `nums`.

When you choose a value `x`, you earn `x` points for one occurrence and all occurrences of `x - 1` and `x + 1` become unavailable. You may keep choosing remaining values. Return the maximum points possible.

**Input**
- `nums`: a list of positive integers.

**Output**
- The maximum total points you can earn.

## Constraints
- `1 <= nums.length <= 2 * 10^4`
- `1 <= nums[i] <= 10^4`

## Examples
```text
Input: nums = [2,2,3,3,3,4]
Output: 9
Explanation: Choosing all three `3`s earns `9` and removes the neighboring values `2` and `4`; no other choice earns more.
```

## Understanding & Intuition
All copies of the same value should be taken together if that value is chosen. After aggregating points per value, the problem becomes House Robber over sorted numeric positions.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively decide whether to take each distinct value after aggregating points.
```python
class Solution:
    def deleteAndEarn(self, nums: list[int]) -> int:
        points = {}
        for x in nums:
            points[x] = points.get(x, 0) + x
        values = sorted(points)
        n = len(values)
        def dfs(i: int, prev_taken: bool) -> int:
            if i == n:
                return 0
            skip = dfs(i + 1, False)
            take = 0
            if not (prev_taken and i > 0 and values[i] == values[i - 1] + 1):
                take = points[values[i]] + dfs(i + 1, True)
            return max(skip, take)
        return dfs(0, False)
```
- **Time:** O(2^m) — **Space:** O(m), where `m` is the number of distinct values.

## Approach 2 — Better
**Idea:** Memoize the distinct-value recursion.
```python
class Solution:
    def deleteAndEarn(self, nums: list[int]) -> int:
        from functools import lru_cache
        points = {}
        for x in nums:
            points[x] = points.get(x, 0) + x
        values = sorted(points)
        n = len(values)
        @lru_cache(None)
        def dfs(i: int, prev_taken: bool) -> int:
            if i == n:
                return 0
            skip = dfs(i + 1, False)
            take = 0
            if not (prev_taken and i > 0 and values[i] == values[i - 1] + 1):
                take = points[values[i]] + dfs(i + 1, True)
            return max(skip, take)
        return dfs(0, False)
```
- **Time:** O(m) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Iterate sorted values with two House Robber states.
```python
class Solution:
    def deleteAndEarn(self, nums: list[int]) -> int:
        points = {}
        for x in nums:
            points[x] = points.get(x, 0) + x
        avoid = using = 0
        prev = None
        for x in sorted(points):
            gain = points[x]
            if prev is not None and x == prev + 1:
                avoid, using = max(avoid, using), avoid + gain
            else:
                best = max(avoid, using)
                avoid, using = best, best + gain
            prev = x
        return max(avoid, using)
```
- **Time:** O(n + m log m) — **Space:** O(m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^m) | O(m) |
| Better | O(m) | O(m) |
| Optimal | O(n + m log m) | O(m) |

## Edge Cases & Pitfalls
- Aggregate by value before applying DP.
- Missing values break adjacency and allow both sides to be taken.

## Related
- House Robber
- Best Time to Buy and Sell Stock
