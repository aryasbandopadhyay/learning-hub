# 13. Regular Expression Matching

- **Difficulty:** Hard
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given a string `s` and a pattern `p`, determine whether the entire string matches the pattern. The
pattern supports two special symbols: `.` matches any single character, and `*` means zero or more
occurrences of the immediately preceding element.

The match must cover all of `s`, not just a substring.

**Input**
- `s`: the input string.
- `p`: the pattern.

**Output**
- A boolean: `true` if `p` matches all of `s`, otherwise `false`.

## Constraints
- 1 <= s.length <= 20
- 1 <= p.length <= 20
- `s` contains only lowercase English letters.
- `p` contains lowercase English letters, `.` and `*`.
- Every `*` has a valid preceding element.

## Examples
```text
Input: s = "aab", p = "c*a*b"
Output: true
Explanation: `c*` matches zero `c` characters, `a*` matches both `a` characters, and `b` matches the final `b`, so the whole string matches.
```

## Understanding & Intuition
Let `dp[i][j]` mean whether `s[i:]` matches `p[j:]`. A normal character or dot consumes one character from both strings. If the next pattern character is `*`, either skip the starred pair or consume one matching character and stay on the same pattern state.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
class SolutionRecursive:
    def isMatch(self, s: str, p: str) -> bool:
        def dfs(i: int, j: int) -> bool:
            if j == len(p):
                return i == len(s)
            first = i < len(s) and (p[j] == s[i] or p[j] == ".")
            if j + 1 < len(p) and p[j + 1] == "*":
                return dfs(i, j + 2) or (first and dfs(i + 1, j))
            return first and dfs(i + 1, j + 1)

        return dfs(0, 0)
```
- **Time:** O(2^(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
class SolutionMemoized:
    def isMatch(self, s: str, p: str) -> bool:
        memo = {}

        def dfs(i: int, j: int) -> bool:
            if (i, j) in memo:
                return memo[(i, j)]
            if j == len(p):
                return i == len(s)
            first = i < len(s) and (p[j] == s[i] or p[j] == ".")
            if j + 1 < len(p) and p[j + 1] == "*":
                memo[(i, j)] = dfs(i, j + 2) or (first and dfs(i + 1, j))
            else:
                memo[(i, j)] = first and dfs(i + 1, j + 1)
            return memo[(i, j)]

        return dfs(0, 0)
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
class Solution:
    def isMatch(self, s: str, p: str) -> bool:
        m, n = len(s), len(p)
        dp = [[False] * (n + 1) for _ in range(m + 1)]
        dp[m][n] = True
        for i in range(m, -1, -1):
            for j in range(n - 1, -1, -1):
                first = i < m and (p[j] == s[i] or p[j] == ".")
                if j + 1 < n and p[j + 1] == "*":
                    dp[i][j] = dp[i][j + 2] or (first and dp[i + 1][j])
                else:
                    dp[i][j] = first and dp[i + 1][j + 1]
        return dp[0][0]
```
- **Time:** O(mn) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(m+n)) | O(m+n) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(mn) |

## Edge Cases & Pitfalls
- `*` applies to the preceding element only.
- Matching must consume the entire input string and pattern.

## Related
- Edit Distance
- Wildcard Matching
