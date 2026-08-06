# 12. Split Array Largest Sum

- **Difficulty:** Hard
- **Pattern:** Binary Search
- **Asked at:** Google, Facebook, Amazon, Microsoft

## Problem
Given a non-negative integer array `nums` and integer `k`, split `nums` into `k` non-empty continuous subarrays to minimize the largest subarray sum. Return that minimized largest sum. Constraints: `1 <= nums.length <= 1000`, `0 <= nums[i] <= 10^6`, `1 <= k <= min(50, nums.length)`.

## Examples
```text
Input: nums = [7,2,5,10,8], k = 2
Output: 18
Explanation: Splitting as [7,2,5] and [10,8] minimizes the largest sum to 18.
```

## Understanding & Intuition
This is binary search on the answer. The predicate "can split with largest sum at most `limit`" is monotonic: if a limit works, any larger limit works. The greedy check starts a new subarray only when adding the next value would exceed the limit.

## Approach 1 — Naive / Brute Force
**Idea:** Use dynamic programming over all split positions.
```python
from functools import lru_cache
from typing import List

class Solution:
    def splitArray(self, nums: List[int], k: int) -> int:
        prefix = [0]
        for num in nums:
            prefix.append(prefix[-1] + num)

        @lru_cache(None)
        def dp(start: int, parts: int) -> int:
            if parts == 1:
                return prefix[len(nums)] - prefix[start]
            best = float("inf")
            for end in range(start + 1, len(nums) - parts + 2):
                left_sum = prefix[end] - prefix[start]
                best = min(best, max(left_sum, dp(end, parts - 1)))
            return best

        return dp(0, k)
```
- **Time:** O(n^2k) — **Space:** O(nk)

## Approach 2 — Better
**Idea:** Bottom-up dynamic programming avoids recursion overhead.
```python
from typing import List

class Solution:
    def splitArray(self, nums: List[int], k: int) -> int:
        n = len(nums)
        prefix = [0]
        for num in nums:
            prefix.append(prefix[-1] + num)

        dp = [[float("inf")] * (k + 1) for _ in range(n + 1)]
        dp[0][0] = 0
        for i in range(1, n + 1):
            for parts in range(1, min(k, i) + 1):
                for prev in range(parts - 1, i):
                    largest = max(dp[prev][parts - 1], prefix[i] - prefix[prev])
                    dp[i][parts] = min(dp[i][parts], largest)
        return int(dp[n][k])
```
- **Time:** O(n^2k) — **Space:** O(nk)

## Approach 3 — Optimal
**Idea:** Binary search the smallest allowed largest sum.
```python
from typing import List

class Solution:
    def splitArray(self, nums: List[int], k: int) -> int:
        def can_split(limit: int) -> bool:
            parts, current = 1, 0
            for num in nums:
                if current + num > limit:
                    parts += 1
                    current = 0
                current += num
            return parts <= k

        left, right = max(nums), sum(nums)
        while left < right:
            mid = (left + right) // 2
            if can_split(mid):
                right = mid
            else:
                left = mid + 1
        return left
```
- **Time:** O(n log S) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2k) | O(nk) |
| Better | O(n^2k) | O(nk) |
| Optimal | O(n log S) | O(1) |

## Edge Cases & Pitfalls
- `k == 1` returns `sum(nums)`.
- `k == len(nums)` returns `max(nums)`.
- The greedy feasibility check is valid because all numbers are non-negative.

## Related
- Capacity To Ship Packages Within D Days
- Allocate Minimum Pages

