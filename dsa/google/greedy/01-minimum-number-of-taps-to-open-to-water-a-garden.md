# 01. Minimum Number of Taps to Open to Water a Garden

- **Difficulty:** Hard
- **Pattern:** greedy
- **Asked at:** Google

## Problem
You have a garden interval `[0, n]` and `n + 1` taps. Tap `i` waters `[i - ranges[i], i + ranges[i]]` clipped to the garden. Return the minimum taps needed to water the whole garden, or `-1` if impossible.

Constraints: `1 <= n <= 10^4`, `len(ranges) == n + 1`, `0 <= ranges[i] <= 100`.

## Examples
```text
Input: n = 5, ranges = [3,4,1,1,0,0]
Output: 1
Explanation: Tap 1 covers every point from 0 to 5.
```

## Understanding & Intuition
Each tap is an interval and the target is to cover one continuous interval. At any current frontier, the best safe greedy move is to pick the reachable interval that extends farthest. Converting each left endpoint to its farthest right endpoint makes the scan identical to Jump Game II.

## Approach 1 — Naive / Brute Force
**Idea:** Dynamic programming over positions, relaxing every tap interval for every position.
```python
class Solution:
    def minTaps(self, n: int, ranges: list[int]) -> int:
        INF = 10 ** 9
        dp = [INF] * (n + 1)
        dp[0] = 0
        intervals = [(max(0, i - r), min(n, i + r)) for i, r in enumerate(ranges)]
        for x in range(n + 1):
            for left, right in intervals:
                if left <= x <= right and dp[left] != INF:
                    dp[x] = min(dp[x], dp[left] + 1)
        return -1 if dp[n] == INF else dp[n]
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort intervals and repeatedly consume all intervals starting before the covered frontier.
```python
class Solution:
    def minTaps(self, n, ranges):
        intervals = sorted((max(0, i - r), min(n, i + r)) for i, r in enumerate(ranges))
        ans = i = covered = 0
        while covered < n:
            best = covered
            while i < len(intervals) and intervals[i][0] <= covered:
                best = max(best, intervals[i][1])
                i += 1
            if best == covered:
                return -1
            ans += 1
            covered = best
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store farthest reach for each left endpoint and greedily count coverage jumps.
```python
class Solution:
    def minTaps(self, n, ranges):
        far = [0] * (n + 1)
        for i, r in enumerate(ranges):
            left, right = max(0, i - r), min(n, i + r)
            far[left] = max(far[left], right)
        ans = curr = nxt = 0
        for x in range(n):
            nxt = max(nxt, far[x])
            if x == curr:
                if nxt <= x:
                    return -1
                ans += 1
                curr = nxt
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A gap at the current frontier makes the answer `-1`.
- Always clip intervals to `[0, n]`.
- Do not count a tap until it actually extends coverage.

## Related
- Jump Game II
- Video Stitching
