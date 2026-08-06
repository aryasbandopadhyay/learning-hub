# 03. Constrained Subsequence Sum

- **Difficulty:** Hard
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given an integer list `nums` and an integer `k`, choose a non-empty subsequence such that adjacent chosen indices differ by at most `k`. Return the maximum possible sum.

Constraints: `1 <= len(nums) <= 100000`; `-10000 <= nums[i] <= 10000`; `1 <= k <= len(nums)`.

## Examples
```text
Input: nums = [10, 2, -10, 5, 20], k = 2
Output: 37
Explanation: Choose 10, 2, 5, and 20; adjacent chosen indices are at most 2 apart.
```

## Understanding & Intuition
Let `dp[i]` be the best valid subsequence ending at `i`. It needs the maximum positive `dp` value from the last `k` positions. This is a sliding-window maximum over dynamic-programming values.

## Approach 1 — Naive / Brute Force
**Idea:** Compute `dp[i]` by scanning all previous allowed indices.
```python
class Solution:
    def constrainedSubsetSum(self, nums: list[int], k: int) -> int:
        n = len(nums)
        dp = nums[:]
        ans = nums[0]
        for i in range(n):
            best = 0
            for j in range(max(0, i - k), i):
                if dp[j] > best:
                    best = dp[j]
            dp[i] = nums[i] + best
            ans = max(ans, dp[i])
        return ans
```
- **Time:** O(nk) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a max-heap of recent `dp` values and discard entries outside the index window.
```python
class Solution:
    def constrainedSubsetSum(self, nums: list[int], k: int) -> int:
        from heapq import heappush, heappop
        heap = []
        ans = nums[0]
        for i, x in enumerate(nums):
            while heap and heap[0][1] < i - k:
                heappop(heap)
            best = -heap[0][0] if heap and -heap[0][0] > 0 else 0
            cur = x + best
            ans = max(ans, cur)
            heappush(heap, (-cur, i))
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain indices of decreasing `dp` values in a deque-like list.
```python
class Solution:
    def constrainedSubsetSum(self, nums: list[int], k: int) -> int:
        n = len(nums)
        dp = [0] * n
        q = []
        head = 0
        ans = nums[0]
        for i, x in enumerate(nums):
            while head < len(q) and q[head] < i - k:
                head += 1
            best = dp[q[head]] if head < len(q) and dp[q[head]] > 0 else 0
            dp[i] = x + best
            ans = max(ans, dp[i])
            while head < len(q) and dp[q[-1]] <= dp[i]:
                q.pop()
            q.append(i)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nk) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The subsequence must be non-empty, so all-negative arrays return the largest element.
- Only positive previous `dp` helps a new ending position.

## Related
- Sliding Window Maximum
- Shortest Subarray with Sum at Least K
