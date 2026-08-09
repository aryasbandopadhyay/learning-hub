# 14. Perfect Squares

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given an integer `n`, return the minimum number of perfect square numbers whose sum is exactly `n`.
A perfect square is an integer such as `1`, `4`, `9`, or `16`.

**Input**
- `n`: the target positive integer.

**Output**
- An integer: the fewest perfect squares needed to sum to `n`.

## Constraints
- 1 <= n <= 10^4

## Examples
```text
Input: n = 12
Output: 3
Explanation: `12` can be written as `4 + 4 + 4`, using `3` perfect squares. It cannot be written using only one or two perfect squares.
```

## Understanding & Intuition
This is coin change where each coin is a perfect square not exceeding `n`. Let `dp[x]` be the fewest squares summing to `x`. For every square `sq <= x`, consider `1 + dp[x - sq]`.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively subtract every square and keep the minimum count.
```python
class Solution:
    def numSquares(self, n: int) -> int:
        squares = [i * i for i in range(1, int(n ** 0.5) + 1)]

        def dfs(rem: int) -> int:
            if rem == 0:
                return 0
            return 1 + min(dfs(rem - sq) for sq in squares if sq <= rem)

        return dfs(n)
```
- **Time:** O(s^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the fewest squares for each remaining value.
```python
class Solution:
    def numSquares(self, n: int) -> int:
        squares = [i * i for i in range(1, int(n ** 0.5) + 1)]
        memo = {0: 0}

        def dfs(rem: int) -> int:
            if rem not in memo:
                memo[rem] = 1 + min(dfs(rem - sq) for sq in squares if sq <= rem)
            return memo[rem]

        return dfs(n)
```
- **Time:** O(n * sqrt(n)) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Bottom-up 1-D DP over all totals.
```python
class Solution:
    def numSquares(self, n: int) -> int:
        squares = [i * i for i in range(1, int(n ** 0.5) + 1)]
        dp = [0] + [float("inf")] * n
        for total in range(1, n + 1):
            for sq in squares:
                if sq > total:
                    break
                dp[total] = min(dp[total], 1 + dp[total - sq])
        return dp[n]
```
- **Time:** O(n * sqrt(n)) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(s^n) | O(n) |
| Better | O(n * sqrt(n)) | O(n) |
| Optimal | O(n * sqrt(n)) | O(n) |

## Edge Cases & Pitfalls
- Include `1` as a square, so an answer always exists.
- Precompute squares only up to `n`.

## Related
- Coin Change
- Sum of Square Numbers
