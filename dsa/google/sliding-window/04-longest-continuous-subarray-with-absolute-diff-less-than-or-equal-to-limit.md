# 04. Longest Continuous Subarray with Absolute Diff Less Than or Equal to Limit

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given an integer list `nums` and an integer `limit`, return the length of the longest contiguous subarray such that the absolute difference between any two elements is at most `limit`.

Constraints: `1 <= len(nums) <= 100000`; `1 <= nums[i] <= 1000000000`; `0 <= limit <= 1000000000`.

## Examples
```text
Input: nums = [8, 2, 4, 7], limit = 4
Output: 2
Explanation: `[2, 4]` is valid; length 3 windows exceed the limit.
```

## Understanding & Intuition
A window is valid exactly when its maximum minus minimum is within `limit`. As the right edge grows, invalidity can only be fixed by moving the left edge. Monotonic queues provide current min and max in O(1).

## Approach 1 — Naive / Brute Force
**Idea:** For each subarray, rescan to find min and max.
```python
class Solution:
    def longestSubarray(self, nums: list[int], limit: int) -> int:
        n = len(nums)
        ans = 0
        for i in range(n):
            for j in range(i, n):
                mn = nums[i]
                mx = nums[i]
                for p in range(i, j + 1):
                    mn = min(mn, nums[p])
                    mx = max(mx, nums[p])
                if mx - mn <= limit:
                    ans = max(ans, j - i + 1)
        return ans
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Extend each left edge while updating min and max incrementally.
```python
class Solution:
    def longestSubarray(self, nums: list[int], limit: int) -> int:
        n = len(nums)
        ans = 0
        for i in range(n):
            mn = nums[i]
            mx = nums[i]
            for j in range(i, n):
                mn = min(mn, nums[j])
                mx = max(mx, nums[j])
                if mx - mn <= limit:
                    ans = max(ans, j - i + 1)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use decreasing and increasing queues of indices for max and min while sliding left.
```python
class Solution:
    def longestSubarray(self, nums: list[int], limit: int) -> int:
        maxq = []
        minq = []
        maxh = 0
        minh = 0
        left = 0
        ans = 0
        for right, x in enumerate(nums):
            while maxh < len(maxq) and nums[maxq[-1]] < x:
                maxq.pop()
            maxq.append(right)
            while minh < len(minq) and nums[minq[-1]] > x:
                minq.pop()
            minq.append(right)
            while nums[maxq[maxh]] - nums[minq[minh]] > limit:
                if maxq[maxh] == left:
                    maxh += 1
                if minq[minh] == left:
                    minh += 1
                left += 1
            ans = max(ans, right - left + 1)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- `limit = 0` allows only windows where all values are equal.
- Remove expired indices when the left edge passes them.

## Related
- Sliding Window Maximum
- Minimum Window Substring
