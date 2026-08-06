# 04. Longest Common Subsequence

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given strings `text1` and `text2`, return the length of their longest common subsequence. A subsequence preserves relative order but may delete characters. Constraints: `1 <= len(text1), len(text2) <= 1000`.

## Examples
```text
Input: text1 = "abcde", text2 = "ace"
Output: 3
Explanation: "ace" is a common subsequence.
```

## Understanding & Intuition
Let `dp[i][j]` be the LCS length for suffixes `text1[i:]` and `text2[j:]`. If characters match, use one character and move both indices. Otherwise skip one character from either string and take the maximum.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
class SolutionRecursive:
    def longestCommonSubsequence(self, text1: str, text2: str) -> int:
        def dfs(i: int, j: int) -> int:
            if i == len(text1) or j == len(text2):
                return 0
            if text1[i] == text2[j]:
                return 1 + dfs(i + 1, j + 1)
            return max(dfs(i + 1, j), dfs(i, j + 1))

        return dfs(0, 0)
```
- **Time:** O(2^(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
class SolutionMemoized:
    def longestCommonSubsequence(self, text1: str, text2: str) -> int:
        memo = {}

        def dfs(i: int, j: int) -> int:
            if i == len(text1) or j == len(text2):
                return 0
            if (i, j) not in memo:
                if text1[i] == text2[j]:
                    memo[(i, j)] = 1 + dfs(i + 1, j + 1)
                else:
                    memo[(i, j)] = max(dfs(i + 1, j), dfs(i, j + 1))
            return memo[(i, j)]

        return dfs(0, 0)
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
class Solution:
    def longestCommonSubsequence(self, text1: str, text2: str) -> int:
        m, n = len(text1), len(text2)
        dp = [0] * (n + 1)
        for i in range(m - 1, -1, -1):
            next_diag = 0
            for j in range(n - 1, -1, -1):
                old = dp[j]
                if text1[i] == text2[j]:
                    dp[j] = 1 + next_diag
                else:
                    dp[j] = max(dp[j], dp[j + 1])
                next_diag = old
        return dp[0]
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(m+n)) | O(m+n) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Empty suffixes contribute zero.
- When characters differ, take max of skipping from either string.

## Related
- Edit Distance
- Distinct Subsequences
