# 11. Binary Subarrays With Sum

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given a binary list `nums` and an integer `goal`, return the number of non-empty contiguous subarrays with sum exactly equal to `goal`.

Implement `Solution.numSubarraysWithSum` with the parameters below and return the requested value.

**Input**
- `nums`: a `list[int]`; the input integer list described above.
- `goal`: a `int`; the required subarray sum.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(nums) <= 100000`
- `nums[i]` is `0` or `1`
- `0 <= goal <= len(nums)`

## Examples
```text
Input: nums = [1, 0, 1, 0, 1], goal = 2
Output: 4
Explanation: The subarrays ending at the middle and last `1` with exactly two ones give four choices.
```

## Understanding & Intuition
Exact counts can be difficult to maintain directly with zeros. For binary arrays, the number with exact sum `goal` equals subarrays with sum at most `goal` minus those with sum at most `goal - 1`. Nonnegative values make the at-most helper a clean sliding window.

## Approach 1 — Naive / Brute Force
**Idea:** Sum every subarray by rescanning it.
```python
class Solution:
    def numSubarraysWithSum(self, nums: list[int], goal: int) -> int:
        n = len(nums)
        ans = 0
        for i in range(n):
            for j in range(i, n):
                total = 0
                for p in range(i, j + 1):
                    total += nums[p]
                if total == goal:
                    ans += 1
        return ans
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count previous prefix sums that would form the desired sum at the current index.
```python
class Solution:
    def numSubarraysWithSum(self, nums: list[int], goal: int) -> int:
        count = {0: 1}
        pref = 0
        ans = 0
        for x in nums:
            pref += x
            ans += count.get(pref - goal, 0)
            count[pref] = count.get(pref, 0) + 1
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Subtract two at-most sliding-window counts.
```python
class Solution:
    def numSubarraysWithSum(self, nums: list[int], goal: int) -> int:
        def at_most(limit: int) -> int:
            if limit < 0:
                return 0
            left = 0
            total = 0
            ans = 0
            for right, x in enumerate(nums):
                total += x
                while total > limit:
                    total -= nums[left]
                    left += 1
                ans += right - left + 1
            return ans
        return at_most(goal) - at_most(goal - 1)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `goal = 0` is handled by `at_most(-1) = 0`.
- The at-most trick relies on nonnegative values.

## Related
- Subarray Sum Equals K
- Subarray Product Less Than K
