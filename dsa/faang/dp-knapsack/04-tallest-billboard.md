# 04. Tallest Billboard

- **Difficulty:** Hard
- **Pattern:** signed subset DP
- **Asked at:** Google, Amazon, Meta

## Problem
Given rod lengths, weld some rods into two supports of equal height. Return the maximum possible support height. Constraints: `1 <= len(rods) <= 20`, `1 <= rods[i] <= 1000`.

## Examples
```text
Input: rods = [1,2,3,6]
Output: 6
Explanation: Use rod 6 on one side and rods 1+2+3 on the other.
```

## Understanding & Intuition
Each rod can be skipped, added to the taller side, or added to the shorter side. Tracking the height difference plus the best shorter side is enough. When the difference is zero, the shorter side is the equal height.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all three choices for every rod.
```python
class Solution:
    def tallestBillboard(self, rods: list[int]) -> int:
        best = 0
        def dfs(i: int, left: int, right: int) -> None:
            nonlocal best
            if i == len(rods):
                if left == right:
                    best = max(best, left)
                return
            dfs(i + 1, left, right)
            dfs(i + 1, left + rods[i], right)
            dfs(i + 1, left, right + rods[i])
        dfs(0, 0, 0)
        return best
```
- **Time:** O(3^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize by index and absolute height difference.
```python
class Solution:
    def tallestBillboard(self, rods: list[int]) -> int:
        from functools import lru_cache
        @lru_cache(None)
        def dp(i: int, diff: int) -> int:
            if i == len(rods):
                return 0 if diff == 0 else -10**9
            x = rods[i]
            skip = dp(i + 1, diff)
            add_taller = dp(i + 1, diff + x)
            add_shorter = min(diff, x) + dp(i + 1, abs(diff - x))
            return max(skip, add_taller, add_shorter)
        return dp(0, 0)
```
- **Time:** O(n * sum) — **Space:** O(n * sum)

## Approach 3 — Optimal
**Idea:** Iteratively map each difference to the best shorter-side height.
```python
class Solution:
    def tallestBillboard(self, rods: list[int]) -> int:
        dp = {0: 0}
        for x in rods:
            cur = dp.copy()
            for diff, short in cur.items():
                dp[diff + x] = max(dp.get(diff + x, 0), short)
                nd = abs(diff - x)
                ns = short + min(diff, x)
                dp[nd] = max(dp.get(nd, 0), ns)
        return dp.get(0, 0)
```
- **Time:** O(n * sum) — **Space:** O(sum)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(3^n) | O(n) |
| Better | O(n * sum) | O(n * sum) |
| Optimal | O(n * sum) | O(sum) |

## Edge Cases & Pitfalls
- Store the shorter height, not the taller height.
- Copy the dictionary before processing a rod.
- Adding to the shorter side can flip the sign of the difference.

## Related
- Target Sum
- Last Stone Weight II
