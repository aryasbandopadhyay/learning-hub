# 14. Distinct Subsequences

- **Difficulty:** Hard
- **Pattern:** DP Strings
- **Asked at:** Amazon, Google, Bloomberg

## Problem
Implement `numDistinct` for **Distinct Subsequences**. Given two strings `s` and `t`, return the number of distinct subsequences of `s` equal to `t`. A subsequence deletes zero or more characters without changing the order of the remaining characters.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `s`: string; input string.
- `t`: string; target string.

**Output**
- A single integer.

## Constraints
- `1 <= len(s), len(t) <= 1000`

## Examples
```text
Input: s = "rabbbit", t = "rabbit"
Output: 3
Explanation: There are three ways to delete one of the three 'b' characters to form "rabbit". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
For each character of `s`, either skip it or use it if it matches the next needed character of `t`. Repeated characters create many overlapping subproblems. Dynamic programming counts those choices without enumerating every subsequence.

## Approach 1 — Naive / Brute Force
**Idea:** fill a full two-dimensional table where `dp[i][j]` counts ways to form `t[:j]` from `s[:i]`.
```python
class Solution:
    def numDistinct(self, s, t):
        m = len(s)
        n = len(t)
        dp = [[0] * (n + 1) for _ in range(m + 1)]
        for i in range(m + 1):
            dp[i][0] = 1
        for i in range(1, m + 1):
            for j in range(1, n + 1):
                dp[i][j] = dp[i - 1][j]
                if s[i - 1] == t[j - 1]:
                    dp[i][j] += dp[i - 1][j - 1]
        return dp[m][n]
```
- **Time:** O(len(s) * len(t)) — **Space:** O(len(s) * len(t))

## Approach 2 — Better
**Idea:** memoize the recursion so each `(i, j)` state is solved once.
```python
class Solution:
    def numDistinct(self, s, t):
        from functools import lru_cache

        @lru_cache(None)
        def dfs(i, j):
            if j == len(t):
                return 1
            if len(s) - i < len(t) - j:
                return 0
            total = dfs(i + 1, j)
            if s[i] == t[j]:
                total += dfs(i + 1, j + 1)
            return total

        return dfs(0, 0)
```
- **Time:** O(len(s) * len(t)) — **Space:** O(len(s) * len(t))

## Approach 3 — Optimal
**Idea:** use one-dimensional DP where `dp[j]` counts ways to form the first `j` characters of `t`.
```python
class Solution:
    def numDistinct(self, s, t):
        dp = [0] * (len(t) + 1)
        dp[0] = 1
        for ch in s:
            for j in range(len(t) - 1, -1, -1):
                if ch == t[j]:
                    dp[j + 1] += dp[j]
        return dp[len(t)]
```
- **Time:** O(len(s) * len(t)) — **Space:** O(len(t))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(len(s) * len(t)) | O(len(s) * len(t)) |
| Better | O(len(s) * len(t)) | O(len(s) * len(t)) |
| Optimal | O(len(s) * len(t)) | O(len(t)) |

## Edge Cases & Pitfalls
- If `t` is longer than `s`, the answer is `0`.
- Iterate the one-dimensional DP backwards so each `s` character is used at most once.
- Python integers handle large counts without overflow.

## Related
- Edit Distance
- Interleaving String
