# 08. Interleaving String

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given strings `s1`, `s2`, and `s3`, return whether `s3` is formed by interleaving `s1` and `s2` while preserving the character order of each. Constraints: `0 <= len(s1), len(s2) <= 100`, `len(s3) <= 200`.

## Examples
```text
Input: s1 = "aabcc", s2 = "dbbca", s3 = "aadbbcbcac"
Output: true
Explanation: Characters can be taken from s1 and s2 in order to form s3.
```

## Understanding & Intuition
Let `dp[i][j]` mean whether `s3[i+j:]` can be formed from suffixes `s1[i:]` and `s2[j:]`. The next character in `s3` must match either the next unused character in `s1` or `s2`. Length mismatch is an immediate false.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
class SolutionRecursive:
    def isInterleave(self, s1: str, s2: str, s3: str) -> bool:
        if len(s1) + len(s2) != len(s3):
            return False

        def dfs(i: int, j: int) -> bool:
            k = i + j
            if k == len(s3):
                return True
            take1 = i < len(s1) and s1[i] == s3[k] and dfs(i + 1, j)
            take2 = j < len(s2) and s2[j] == s3[k] and dfs(i, j + 1)
            return take1 or take2

        return dfs(0, 0)
```
- **Time:** O(2^(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
class SolutionMemoized:
    def isInterleave(self, s1: str, s2: str, s3: str) -> bool:
        if len(s1) + len(s2) != len(s3):
            return False
        memo = {}

        def dfs(i: int, j: int) -> bool:
            k = i + j
            if k == len(s3):
                return True
            if (i, j) not in memo:
                take1 = i < len(s1) and s1[i] == s3[k] and dfs(i + 1, j)
                take2 = j < len(s2) and s2[j] == s3[k] and dfs(i, j + 1)
                memo[(i, j)] = take1 or take2
            return memo[(i, j)]

        return dfs(0, 0)
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
class Solution:
    def isInterleave(self, s1: str, s2: str, s3: str) -> bool:
        if len(s1) + len(s2) != len(s3):
            return False
        n = len(s2)
        dp = [False] * (n + 1)
        dp[n] = True
        for i in range(len(s1), -1, -1):
            for j in range(n, -1, -1):
                if i == len(s1) and j == n:
                    continue
                k = i + j
                dp[j] = (i < len(s1) and s1[i] == s3[k] and dp[j]) or (
                    j < n and s2[j] == s3[k] and dp[j + 1]
                )
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
- Length mismatch must return false before DP.
- Be careful that index in `s3` is always `i + j`.

## Related
- Longest Common Subsequence
- Edit Distance
