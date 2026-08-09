# 12. Maximum Average Subarray I

- **Difficulty:** Easy
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given an integer array `nums` and an integer `k`, find the maximum average value over all contiguous subarrays of length exactly `k`.

**Input**
- `nums`: a list of integers.
- `k`: the fixed subarray length.

**Output**
- The maximum average as a floating-point number.

## Constraints
- `1 <= k <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

## Examples
```text
Input: nums = [1,12,-5,-6,50,3], k = 4
Output: 12.75
Explanation: The length-4 subarray `[12,-5,-6,50]` has sum `51`, so its average is `51 / 4 = 12.75`, the maximum.
```

## Understanding & Intuition
Since every candidate window has the same length, maximizing average is equivalent to maximizing sum. Adjacent length-`k` windows differ by one outgoing and one incoming element. A rolling sum gives constant work per shift.

## Approach 1 — Naive / Brute Force
**Idea:** Sum each length-`k` window independently.
```python
from typing import List

class Solution:
    def findMaxAverage(self, nums: List[int], k: int) -> float:
        best = -float("inf")
        for left in range(len(nums) - k + 1):
            best = max(best, sum(nums[left:left + k]))
        return best / k
```
- **Time:** O(nk) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build prefix sums so each fixed-window sum is O(1).
```python
from typing import List

class Solution:
    def findMaxAverage(self, nums: List[int], k: int) -> float:
        prefix = [0]
        for num in nums:
            prefix.append(prefix[-1] + num)
        best = -float("inf")
        for right in range(k, len(nums) + 1):
            best = max(best, prefix[right] - prefix[right - k])
        return best / k
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep a rolling sum of the current length-`k` window.
```python
from typing import List

class Solution:
    def findMaxAverage(self, nums: List[int], k: int) -> float:
        window = sum(nums[:k])
        best = window
        for right in range(k, len(nums)):
            # Slide by adding the new value and removing the value k steps back.
            window += nums[right] - nums[right - k]
            best = max(best, window)
        return best / k
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nk) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Negative numbers require initializing from an actual window, not `0`.
- The window length must be exactly `k`.
- Return a float average, not the sum.

## Related
- Minimum Size Subarray Sum
- Sliding Window Maximum

