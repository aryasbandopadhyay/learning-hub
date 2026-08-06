# 09. Integer Break

- **Difficulty:** Medium
- **Pattern:** unbounded integer partition DP
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given integer `n`, split it into at least two positive integers and return the maximum possible product. Constraints: `2 <= n <= 58`.

## Examples
```text
Input: n = 10
Output: 36
Explanation: Split 10 as 3 + 3 + 4, giving product 36.
```

## Understanding & Intuition
Every first cut leaves a remainder that can either stay whole or be split again. This mirrors unbounded knapsack over integer lengths. The mathematical optimum uses as many 3s as possible, except avoid a leftover 1.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try each first cut.
```python
class Solution:
    def integerBreak(self, n: int) -> int:
        def best(x: int) -> int:
            ans = 0
            for first in range(1, x):
                ans = max(ans, first * (x - first), first * best(x - first))
            return ans
        return best(n)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the best product for each remaining integer.
```python
class Solution:
    def integerBreak(self, n: int) -> int:
        from functools import lru_cache
        @lru_cache(None)
        def best(x: int) -> int:
            ans = 0
            for first in range(1, x):
                ans = max(ans, first * (x - first), first * best(x - first))
            return ans
        return best(n)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the fact that factors of 3 maximize product.
```python
class Solution:
    def integerBreak(self, n: int) -> int:
        if n <= 3:
            return n - 1
        product = 1
        while n > 4:
            product *= 3
            n -= 3
        return product * n
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The split must contain at least two integers.
- For `n = 2` or `n = 3`, the answer is `n - 1`.
- Replace `3 + 1` with `2 + 2`.

## Related
- Form Largest Integer With Digits That Add up to Target
- Perfect Squares
