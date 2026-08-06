# 02. Ones and Zeroes

- **Difficulty:** Medium
- **Pattern:** two-dimensional 0/1 knapsack
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Given binary strings `strs` and capacities `m` zeros and `n` ones, return the largest number of strings you can choose without exceeding either capacity. Constraints: `1 <= len(strs) <= 600`, `0 <= m,n <= 100`.

## Examples
```text
Input: strs = ["10","0001","111001","1","0"], m = 5, n = 3
Output: 4
Explanation: Choose "10", "0001", "1", and "0".
```

## Understanding & Intuition
Each string is an item with two weights: zeros and ones. Its value is one chosen string. Because every string can be used once, capacity loops must run backward.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively choose or skip every string.
```python
class Solution:
    def findMaxForm(self, strs: list[str], m: int, n: int) -> int:
        counts = [(s.count("0"), s.count("1")) for s in strs]
        def dfs(i: int, z: int, o: int) -> int:
            if i == len(counts):
                return 0
            ans = dfs(i + 1, z, o)
            cz, co = counts[i]
            if z >= cz and o >= co:
                ans = max(ans, 1 + dfs(i + 1, z - cz, o - co))
            return ans
        return dfs(0, m, n)
```
- **Time:** O(2^s) — **Space:** O(s)

## Approach 2 — Better
**Idea:** Memoize by index and remaining capacities.
```python
class Solution:
    def findMaxForm(self, strs: list[str], m: int, n: int) -> int:
        from functools import lru_cache
        counts = [(s.count("0"), s.count("1")) for s in strs]
        @lru_cache(None)
        def dp(i: int, z: int, o: int) -> int:
            if i == len(counts):
                return 0
            ans = dp(i + 1, z, o)
            cz, co = counts[i]
            if z >= cz and o >= co:
                ans = max(ans, 1 + dp(i + 1, z - cz, o - co))
            return ans
        return dp(0, m, n)
```
- **Time:** O(smn) — **Space:** O(smn)

## Approach 3 — Optimal
**Idea:** Compress away the string index into a 2D table.
```python
class Solution:
    def findMaxForm(self, strs: list[str], m: int, n: int) -> int:
        dp = [[0] * (n + 1) for _ in range(m + 1)]
        for s in strs:
            z = s.count("0")
            o = len(s) - z
            for zeros in range(m, z - 1, -1):
                for ones in range(n, o - 1, -1):
                    dp[zeros][ones] = max(dp[zeros][ones], dp[zeros - z][ones - o] + 1)
        return dp[m][n]
```
- **Time:** O(smn) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^s) | O(s) |
| Better | O(smn) | O(smn) |
| Optimal | O(smn) | O(mn) |

## Edge Cases & Pitfalls
- Strings exceeding one capacity may still be skipped.
- Iterate both capacities backward.
- Count zeros and ones consistently for every approach.

## Related
- 0/1 Knapsack
- Maximum Length of a Concatenated String with Unique Characters
