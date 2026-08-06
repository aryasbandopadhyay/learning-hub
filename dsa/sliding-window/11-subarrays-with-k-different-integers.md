# 11. Subarrays with K Different Integers

- **Difficulty:** Hard
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, Uber

## Problem
Given integer array `nums` and integer `k`, return the number of contiguous subarrays containing exactly `k` distinct integers. Constraints: `1 <= len(nums) <= 2 * 10^4`, `1 <= nums[i], k <= len(nums)`.

## Examples
```text
Input: nums = [1,2,1,2,3], k = 2
Output: 7
Explanation: The valid subarrays are [1,2], [2,1], [1,2], [2,3], [1,2,1], [2,1,2], and [1,2,1,2].
```

## Understanding & Intuition
Counting exactly `k` distinct values is harder than maintaining one valid window. A useful identity is `exactly(k) = atMost(k) - atMost(k-1)`. Another refinement tracks two left boundaries for at most `k` and at most `k-1` simultaneously.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every subarray and count its distinct values.
```python
from typing import List

class Solution:
    def subarraysWithKDistinct(self, nums: List[int], k: int) -> int:
        ans = 0
        for left in range(len(nums)):
            seen = set()
            for right in range(left, len(nums)):
                seen.add(nums[right])
                if len(seen) == k:
                    ans += 1
                elif len(seen) > k:
                    break
        return ans
```
- **Time:** O(n^2) — **Space:** O(k)

## Approach 2 — Better
**Idea:** Count subarrays with at most `k` distinct and subtract at most `k - 1`.
```python
from typing import List

class Solution:
    def subarraysWithKDistinct(self, nums: List[int], k: int) -> int:
        def at_most(limit: int) -> int:
            counts = {}
            left = total = 0
            for right, value in enumerate(nums):
                counts[value] = counts.get(value, 0) + 1
                while len(counts) > limit:
                    counts[nums[left]] -= 1
                    if counts[nums[left]] == 0:
                        del counts[nums[left]]
                    left += 1
                # Every start from left..right gives an at-most-limit subarray.
                total += right - left + 1
            return total

        return at_most(k) - at_most(k - 1)
```
- **Time:** O(n) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Maintain two windows in one pass: one with at most `k`, one with at most `k - 1`.
```python
from typing import List

class Solution:
    def subarraysWithKDistinct(self, nums: List[int], k: int) -> int:
        count_k = {}
        count_k_minus = {}
        left_k = left_k_minus = ans = 0
        for right, value in enumerate(nums):
            count_k[value] = count_k.get(value, 0) + 1
            count_k_minus[value] = count_k_minus.get(value, 0) + 1

            while len(count_k) > k:
                count_k[nums[left_k]] -= 1
                if count_k[nums[left_k]] == 0:
                    del count_k[nums[left_k]]
                left_k += 1
            while len(count_k_minus) > k - 1:
                count_k_minus[nums[left_k_minus]] -= 1
                if count_k_minus[nums[left_k_minus]] == 0:
                    del count_k_minus[nums[left_k_minus]]
                left_k_minus += 1

            # Starts in [left_k, left_k_minus) have exactly k distinct values.
            ans += left_k_minus - left_k
        return ans
```
- **Time:** O(n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(k) |
| Better | O(n) | O(k) |
| Optimal | O(n) | O(k) |

## Edge Cases & Pitfalls
- The answer can exceed `n`, so count all valid starts for each right endpoint.
- `atMost(k - 1)` is essential; do not try to count exactly with one simple window.
- Delete keys when their frequency becomes zero.

## Related
- Fruit Into Baskets
- Count Number of Nice Subarrays

