# 02. Shortest Subarray With Sum at Least K

- **Difficulty:** Hard
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Meta

## Problem
Given an integer array `nums` and integer `k`, return the length of the shortest non-empty contiguous subarray whose sum is at least `k`. Return `-1` if no such subarray exists. `1 <= len(nums) <= 10^5`, `-10^5 <= nums[i] <= 10^5`, and `1 <= k <= 10^9`.

## Examples
```text
Input: nums = [2,-1,2], k = 3
Output: 3
Explanation: The whole array has sum 3 and is shortest.
```

## Understanding & Intuition
Negative numbers break the normal positive-only sliding window because expanding can reduce the sum. Prefix sums convert any subarray into a difference of two prefixes. A monotonic deque keeps only useful candidate start prefixes.

## Approach 1 — Naive / Brute Force
**Idea:** Check every start and end, updating the best length when the running sum reaches `k`.
```python
class Solution:
    def shortestSubarray(self, nums: list[int], k: int) -> int:
        n = len(nums)
        best = n + 1
        for i in range(n):
            total = 0
            for j in range(i, n):
                total += nums[j]
                if total >= k:
                    best = min(best, j - i + 1)
        return -1 if best == n + 1 else best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use prefix sums and test window lengths from shortest to longest.
```python
class Solution:
    def shortestSubarray(self, nums: list[int], k: int) -> int:
        n = len(nums)
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        for length in range(1, n + 1):
            for i in range(0, n - length + 1):
                if prefix[i + length] - prefix[i] >= k:
                    return length
        return -1
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain increasing prefix sums in a deque; pop front when it forms a valid shortest subarray and pop back when a newer prefix is smaller.
```python
class Solution:
    def shortestSubarray(self, nums: list[int], k: int) -> int:
        from collections import deque
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        dq = deque()
        best = len(nums) + 1
        for i, cur in enumerate(prefix):
            while dq and cur - prefix[dq[0]] >= k:
                best = min(best, i - dq.popleft())
            while dq and prefix[dq[-1]] >= cur:
                dq.pop()
            dq.append(i)
        return -1 if best == len(nums) + 1 else best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Negative values mean a simple sliding window is invalid.
- A single element can be the answer.
- Remove dominated larger prefix sums from the deque back.

## Related
- Minimum Size Subarray Sum
- Continuous Subarray Sum
