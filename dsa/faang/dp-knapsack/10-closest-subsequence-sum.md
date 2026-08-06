# 10. Closest Subsequence Sum

- **Difficulty:** Hard
- **Pattern:** meet-in-the-middle subset sum
- **Asked at:** Google, Meta, Amazon

## Problem
Given `nums` and `goal`, return the minimum absolute difference between `goal` and the sum of any subsequence. Constraints: `1 <= len(nums) <= 40`, values may be negative.

## Examples
```text
Input: nums = [5,-7,3,5], goal = 6
Output: 0
Explanation: The subsequence [5,-7,3,5] sums to 6.
```

## Understanding & Intuition
Forty numbers make all subsets too many, but half-subsets are manageable. Generate all sums for each half and binary search the best complement. Negative numbers make standard array-index subset DP unsuitable.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every subsequence sum.
```python
class Solution:
    def minAbsDifference(self, nums: list[int], goal: int) -> int:
        best = abs(goal)
        def dfs(i: int, total: int) -> None:
            nonlocal best
            if i == len(nums):
                best = min(best, abs(goal - total))
                return
            dfs(i + 1, total)
            dfs(i + 1, total + nums[i])
        dfs(0, 0)
        return best
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Maintain the set of all reachable sums.
```python
class Solution:
    def minAbsDifference(self, nums: list[int], goal: int) -> int:
        sums = {0}
        for x in nums:
            sums |= {s + x for s in sums}
        return min(abs(goal - s) for s in sums)
```
- **Time:** O(2^n) — **Space:** O(2^n)

## Approach 3 — Optimal
**Idea:** Meet in the middle and binary search right-half complements.
```python
class Solution:
    def minAbsDifference(self, nums: list[int], goal: int) -> int:
        from bisect import bisect_left
        def build(arr: list[int]) -> list[int]:
            res = [0]
            for x in arr:
                res += [s + x for s in res]
            return res
        mid = len(nums) // 2
        left = build(nums[:mid])
        right = sorted(build(nums[mid:]))
        ans = abs(goal)
        for s in left:
            need = goal - s
            i = bisect_left(right, need)
            if i < len(right):
                ans = min(ans, abs(need - right[i]))
            if i > 0:
                ans = min(ans, abs(need - right[i - 1]))
            if ans == 0:
                return 0
        return ans
```
- **Time:** O(2^(n/2) * n) — **Space:** O(2^(n/2))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(2^n) | O(2^n) |
| Optimal | O(2^(n/2) * n) | O(2^(n/2)) |

## Edge Cases & Pitfalls
- The empty subsequence is allowed.
- Check both the insertion point and its predecessor.
- Negative values are naturally handled by generated sums.

## Related
- Last Stone Weight II
- Partition Array Into Two Arrays to Minimize Sum Difference
