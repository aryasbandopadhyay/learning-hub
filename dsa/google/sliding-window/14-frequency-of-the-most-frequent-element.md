# 14. Frequency of the Most Frequent Element

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google, LeetCode

## Problem
Given an integer array `nums` and an integer `k`, you may increment any element by `1` in one operation. Return the maximum possible frequency of any element after performing at most `k` operations.

Constraints: `1 <= len(nums) <= 100000`; `1 <= nums[i] <= 100000`; `0 <= k <= 100000`.

## Examples
```text
Input: nums = [1, 2, 4], k = 5
Output: 3
Explanation: Increment 1 twice and 2 twice to make all elements equal to 4 using 4 operations.
```

## Understanding & Intuition
After sorting, it is never useful to make a group equal to a value outside that group; choose the rightmost value as the target. For a sorted window ending at `right`, the cost is `nums[right] * window_size - window_sum`. Keep the largest window whose cost is at most `k`.

## Approach 1 — Naive / Brute Force
**Idea:** Sort the numbers, then for each target index expand left while counting the increments needed to match that target.
```python
class Solution:
    def maxFrequency(self, nums: list[int], k: int) -> int:
        nums = sorted(nums)
        best = 1
        n = len(nums)
        for right in range(n):
            cost = 0
            count = 1
            for left in range(right - 1, -1, -1):
                cost += nums[right] - nums[left]
                if cost > k:
                    break
                count += 1
            best = max(best, count)
        return best
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use prefix sums and binary search the earliest valid left boundary for each target index.
```python
class Solution:
    def maxFrequency(self, nums: list[int], k: int) -> int:
        nums = sorted(nums)
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        best = 1
        for right, target in enumerate(nums):
            lo, hi = 0, right
            while lo < hi:
                mid = (lo + hi) // 2
                total = prefix[right + 1] - prefix[mid]
                cost = target * (right - mid + 1) - total
                if cost <= k:
                    hi = mid
                else:
                    lo = mid + 1
            best = max(best, right - lo + 1)
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain the largest sorted window whose elements can all be raised to the rightmost value.
```python
class Solution:
    def maxFrequency(self, nums: list[int], k: int) -> int:
        nums = sorted(nums)
        left = 0
        total = 0
        best = 1
        for right, value in enumerate(nums):
            total += value
            while value * (right - left + 1) - total > k:
                total -= nums[left]
                left += 1
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Sorting is essential because operations can only increment smaller values up to a target.
- The optimal sliding window is variable length, not fixed length.
- `k = 0` means the answer is the largest existing duplicate count.

## Related
- Longest Repeating Character Replacement
- Minimum Size Subarray Sum
