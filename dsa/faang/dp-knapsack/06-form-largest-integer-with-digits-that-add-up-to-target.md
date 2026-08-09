# 06. Form Largest Integer With Digits That Add up to Target

- **Difficulty:** Hard
- **Pattern:** unbounded knapsack reconstruction
- **Asked at:** Google, Amazon, Microsoft

## Problem
Implement `largestNumber` for **Form Largest Integer With Digits That Add up to Target**. Given `cost[0..8]` for digits `1..9`, return the numerically largest integer whose digit costs sum to `target`, or `"0"` if impossible.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

This judge compares exactly; return the numerically largest string, meaning maximum length first and then larger digits earlier.

**Input**
- `cost`: list; digit costs.
- `target`: integer; target value or string.

**Output**
- A string. This judge compares exactly; return the numerically largest string, meaning maximum length first and then larger digits earlier.

## Constraints
- `len(cost) == 9`, `1 <= target <= 5000`

## Examples
```text
Input: cost = [4,3,2,5,6,7,2,5,5], target = 9
Output: "7772"
Explanation: Costs 2+2+2+3 = 9, and four digits beat any shorter number. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
A larger number first has more digits; for equal length, larger leading digits win. So maximize digit count by total cost, then greedily reconstruct from digit 9 down to 1. This is unbounded knapsack because digits may repeat.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try appending every digit and compare candidate strings.
```python
class Solution:
    def largestNumber(self, cost: list[int], target: int) -> str:
        def better(a: str, b: str) -> str:
            if a == "#":
                return b
            if b == "#":
                return a
            if len(a) != len(b):
                return a if len(a) > len(b) else b
            return a if a > b else b
        def dfs(rem: int) -> str:
            if rem == 0:
                return ""
            if rem < 0:
                return "#"
            ans = "#"
            for d in range(9, 0, -1):
                tail = dfs(rem - cost[d - 1])
                if tail != "#":
                    ans = better(ans, str(d) + tail)
            return ans
        ans = dfs(target)
        return "0" if ans == "#" else ans
```
- **Time:** O(9^target) — **Space:** O(target)

## Approach 2 — Better
**Idea:** Memoize the best string for each remaining cost.
```python
class Solution:
    def largestNumber(self, cost: list[int], target: int) -> str:
        from functools import lru_cache
        def better(a: str, b: str) -> str:
            if a == "#":
                return b
            if b == "#":
                return a
            if len(a) != len(b):
                return a if len(a) > len(b) else b
            return a if a > b else b
        @lru_cache(None)
        def dp(rem: int) -> str:
            if rem == 0:
                return ""
            if rem < 0:
                return "#"
            ans = "#"
            for d in range(9, 0, -1):
                tail = dp(rem - cost[d - 1])
                if tail != "#":
                    ans = better(ans, str(d) + tail)
            return ans
        ans = dp(target)
        return "0" if ans == "#" else ans
```
- **Time:** O(9 * target^2) — **Space:** O(target^2)

## Approach 3 — Optimal
**Idea:** Maximize digit count, then greedily emit the largest possible next digit.
```python
class Solution:
    def largestNumber(self, cost: list[int], target: int) -> str:
        NEG = -10**9
        dp = [NEG] * (target + 1)
        dp[0] = 0
        for t in range(1, target + 1):
            for c in cost:
                if t >= c and dp[t - c] != NEG:
                    dp[t] = max(dp[t], dp[t - c] + 1)
        if dp[target] < 0:
            return "0"
        ans = []
        rem = target
        for d in range(9, 0, -1):
            c = cost[d - 1]
            while rem >= c and dp[rem] == dp[rem - c] + 1:
                ans.append(str(d))
                rem -= c
        return "".join(ans)
```
- **Time:** O(9 * target + answer length) — **Space:** O(target)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(9^target) | O(target) |
| Better | O(9 * target^2) | O(target^2) |
| Optimal | O(9 * target) | O(target) |

## Edge Cases & Pitfalls
- More digits always beat a shorter number.
- Return `"0"` if no exact cost is possible.
- Reconstruct from digit 9 down to digit 1.

## Related
- Coin Change II
- Integer Break
