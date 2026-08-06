# 13. Count Number of Nice Subarrays

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Microsoft, Bloomberg

## Problem
Given integer array `nums` and integer `k`, return the number of contiguous subarrays containing exactly `k` odd numbers. Constraints: `1 <= len(nums) <= 5 * 10^4`, `1 <= nums[i] <= 10^5`, `1 <= k <= len(nums)`.

## Examples
```text
Input: nums = [1,1,2,1,1], k = 3
Output: 2
Explanation: The nice subarrays are [1,1,2,1] and [1,2,1,1].
```

## Understanding & Intuition
Only the positions of odd numbers determine whether a subarray is nice. The same `exactly = atMost(k) - atMost(k-1)` trick works when the limited resource is odd count. A positions-based formula can count choices around each group of `k` odds directly.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate subarrays and count odds until the count exceeds `k`.
```python
from typing import List

class Solution:
    def numberOfSubarrays(self, nums: List[int], k: int) -> int:
        ans = 0
        for left in range(len(nums)):
            odds = 0
            for right in range(left, len(nums)):
                odds += nums[right] % 2
                if odds == k:
                    ans += 1
                elif odds > k:
                    break
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count subarrays with at most `k` odds and subtract those with at most `k - 1`.
```python
from typing import List

class Solution:
    def numberOfSubarrays(self, nums: List[int], k: int) -> int:
        def at_most(limit: int) -> int:
            left = odds = total = 0
            for right, value in enumerate(nums):
                odds += value % 2
                while odds > limit:
                    odds -= nums[left] % 2
                    left += 1
                total += right - left + 1
            return total

        return at_most(k) - at_most(k - 1)
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use odd indexes; each group of `k` odds has independent left and right extension choices.
```python
from typing import List

class Solution:
    def numberOfSubarrays(self, nums: List[int], k: int) -> int:
        odds = [-1]
        for i, value in enumerate(nums):
            if value % 2:
                odds.append(i)
        odds.append(len(nums))

        ans = 0
        for i in range(1, len(odds) - k):
            # Choose a start after previous odd and an end before next odd.
            left_choices = odds[i] - odds[i - 1]
            right_choices = odds[i + k] - odds[i + k - 1]
            ans += left_choices * right_choices
        return ans
```
- **Time:** O(n) — **Space:** O(number of odds)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(number of odds) |

## Edge Cases & Pitfalls
- Even numbers can extend a nice subarray without changing odd count.
- In the at-most helper, `limit` may be `0`.
- Sentinels simplify counting starts before the first odd and ends after the last odd.

## Related
- Subarrays with K Different Integers
- Max Consecutive Ones III

