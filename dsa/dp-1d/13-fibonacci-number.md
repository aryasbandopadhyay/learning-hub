# 13. Fibonacci Number

- **Difficulty:** Easy
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
The Fibonacci sequence is defined by `F(0) = 0`, `F(1) = 1`, and `F(n) = F(n - 1) + F(n - 2)` for
`n > 1`. Given `n`, return `F(n)`.

**Input**
- `n`: the Fibonacci index.

**Output**
- An integer: the `n`th Fibonacci number.

## Constraints
- 0 <= n <= 30

## Examples
```text
Input: n = 4
Output: 3
Explanation: `F(2) = 1`, `F(3) = 2`, and `F(4) = 3`, so the answer is `3`.
```

## Understanding & Intuition
The state is directly `dp[i] = F(i)`. Each value depends only on the previous two values. This makes it ideal for rolling-variable tabulation.

## Approach 1 — Naive / Brute Force
**Idea:** Direct recursive definition of Fibonacci.
```python
class Solution:
    def fib(self, n: int) -> int:
        if n < 2:
            return n
        return self.fib(n - 1) + self.fib(n - 2)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize each Fibonacci number.
```python
class Solution:
    def fib(self, n: int) -> int:
        memo = {0: 0, 1: 1}

        def dfs(k: int) -> int:
            if k not in memo:
                memo[k] = dfs(k - 1) + dfs(k - 2)
            return memo[k]

        return dfs(n)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterate while keeping only the previous two Fibonacci numbers.
```python
class Solution:
    def fib(self, n: int) -> int:
        if n < 2:
            return n
        prev2, prev1 = 0, 1
        for _ in range(2, n + 1):
            prev2, prev1 = prev1, prev1 + prev2
        return prev1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `n = 0` returns `0`.
- Avoid confusing 0-indexed Fibonacci with stair-count variants.

## Related
- Climbing Stairs
- Tribonacci Number
