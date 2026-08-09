# 10. Distinct Subsequences

- **Difficulty:** Hard
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Bloomberg

## Problem
Given strings `s` and `t`, count how many distinct subsequences of `s` are equal to `t`. A
subsequence is formed by deleting zero or more characters without changing the order of the remaining
characters.

**Input**
- `s`: the source string.
- `t`: the target string.

**Output**
- An integer: the number of subsequences of `s` equal to `t`.

## Constraints
- 1 <= s.length, t.length <= 1000
- `s` and `t` consist of English letters.
- The answer fits in a 32-bit signed integer.

## Examples
```text
Input: s = "rabbbit", t = "rabbit"
Output: 3
Explanation: There are three ways to delete one of the three `b` characters from `"rabbbit"` so that the remaining string is `"rabbit"`.
```

## Understanding & Intuition
Let `dp[i][j]` be the ways to form `t[j:]` from `s[i:]`. If characters match, either use `s[i]` for `t[j]` or skip it. If they do not match, only skipping `s[i]` is possible.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
class SolutionRecursive:
    def numDistinct(self, s: str, t: str) -> int:
        def dfs(i: int, j: int) -> int:
            if j == len(t):
                return 1
            if i == len(s):
                return 0
            ways = dfs(i + 1, j)
            if s[i] == t[j]:
                ways += dfs(i + 1, j + 1)
            return ways

        return dfs(0, 0)
```
- **Time:** O(2^m) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
class SolutionMemoized:
    def numDistinct(self, s: str, t: str) -> int:
        memo = {}

        def dfs(i: int, j: int) -> int:
            if j == len(t):
                return 1
            if i == len(s):
                return 0
            if (i, j) not in memo:
                memo[(i, j)] = dfs(i + 1, j)
                if s[i] == t[j]:
                    memo[(i, j)] += dfs(i + 1, j + 1)
            return memo[(i, j)]

        return dfs(0, 0)
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
class Solution:
    def numDistinct(self, s: str, t: str) -> int:
        n = len(t)
        dp = [0] * (n + 1)
        dp[n] = 1
        for i in range(len(s) - 1, -1, -1):
            # Move left-to-right over suffix index using a saved diagonal.
            prev_diag = 1
            for j in range(n - 1, -1, -1):
                old = dp[j]
                if s[i] == t[j]:
                    dp[j] += prev_diag
                prev_diag = old
        return dp[0]
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^m) | O(m+n) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Empty target can always be formed once.
- If remaining `s` is shorter than remaining `t`, the state contributes zero.

## Related
- Longest Common Subsequence
- Edit Distance
