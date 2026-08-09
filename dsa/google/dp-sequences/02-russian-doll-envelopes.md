# 02. Russian Doll Envelopes

- **Difficulty:** Hard
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Facebook, Amazon

## Problem
You are given envelopes, each represented as `[width, height]`.

One envelope can be placed inside another only when both its width and height are strictly smaller. Return the maximum number of envelopes that can be nested in a chain.

**Input**
- `envelopes`: a list of `[width, height]` pairs.

**Output**
- The maximum length of a strictly nesting envelope chain.

## Constraints
- `1 <= envelopes.length <= 10^5`
- `1 <= width, height <= 10^5`

## Examples
```text
Input: envelopes = [[5,4],[6,4],[6,7],[2,3]]
Output: 3
Explanation: A maximum chain is `[2,3] -> [5,4] -> [6,7]`, so the answer is `3`.
```

## Understanding & Intuition
Sorting turns the two-dimensional nesting order into a sequence problem. For equal widths, heights must be ordered descending so an LIS on heights never uses two envelopes of the same width.

## Approach 1 — Naive / Brute Force
**Idea:** Try every envelope as the next nested choice after sorting.
```python
class Solution:
    def maxEnvelopes(self, envelopes: list[list[int]]) -> int:
        env = sorted(envelopes)
        n = len(env)
        def dfs(prev: int, start: int) -> int:
            best = 0
            for i in range(start, n):
                if prev == -1 or (env[prev][0] < env[i][0] and env[prev][1] < env[i][1]):
                    best = max(best, 1 + dfs(i, i + 1))
            return best
        return dfs(-1, 0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort by both dimensions and compute the longest valid chain ending at each envelope.
```python
class Solution:
    def maxEnvelopes(self, envelopes: list[list[int]]) -> int:
        env = sorted(envelopes)
        n = len(env)
        dp = [1] * n
        for i in range(n):
            for j in range(i):
                if env[j][0] < env[i][0] and env[j][1] < env[i][1]:
                    dp[i] = max(dp[i], dp[j] + 1)
        return max(dp, default=0)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort widths ascending and equal widths descending, then run patience-sorting LIS on heights.
```python
class Solution:
    def maxEnvelopes(self, envelopes: list[list[int]]) -> int:
        from bisect import bisect_left
        env = sorted(envelopes, key=lambda x: (x[0], -x[1]))
        tails = []
        for _, h in env:
            i = bisect_left(tails, h)
            if i == len(tails):
                tails.append(h)
            else:
                tails[i] = h
        return len(tails)
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Equal widths cannot be chained; sort their heights descending before LIS.
- Strictly smaller means both dimensions must improve.

## Related
- Longest Increasing Subsequence
- Maximum Length of Pair Chain
