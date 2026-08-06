# 11. Edit Distance

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given two strings, return the minimum number of insertions, deletions, and replacements needed to convert `word1` into `word2`. Constraints: `0 <= len(word1), len(word2) <= 500`.

## Examples
```text
Input: word1 = "horse", word2 = "ros"
Output: 3
Explanation: horse -> rorse -> rose -> ros.
```

## Understanding & Intuition
Let `dp[i][j]` be the edit distance between suffixes `word1[i:]` and `word2[j:]`. Equal characters move both pointers for free. Otherwise, try insert, delete, and replace, then add one operation.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
class SolutionRecursive:
    def minDistance(self, word1: str, word2: str) -> int:
        def dfs(i: int, j: int) -> int:
            if i == len(word1):
                return len(word2) - j
            if j == len(word2):
                return len(word1) - i
            if word1[i] == word2[j]:
                return dfs(i + 1, j + 1)
            insert = dfs(i, j + 1)
            delete = dfs(i + 1, j)
            replace = dfs(i + 1, j + 1)
            return 1 + min(insert, delete, replace)

        return dfs(0, 0)
```
- **Time:** O(3^(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
class SolutionMemoized:
    def minDistance(self, word1: str, word2: str) -> int:
        memo = {}

        def dfs(i: int, j: int) -> int:
            if i == len(word1):
                return len(word2) - j
            if j == len(word2):
                return len(word1) - i
            if (i, j) not in memo:
                if word1[i] == word2[j]:
                    memo[(i, j)] = dfs(i + 1, j + 1)
                else:
                    memo[(i, j)] = 1 + min(dfs(i, j + 1), dfs(i + 1, j), dfs(i + 1, j + 1))
            return memo[(i, j)]

        return dfs(0, 0)
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
class Solution:
    def minDistance(self, word1: str, word2: str) -> int:
        n = len(word2)
        dp = [n - j for j in range(n + 1)]
        for i in range(len(word1) - 1, -1, -1):
            next_diag = dp[n]
            dp[n] = len(word1) - i
            for j in range(n - 1, -1, -1):
                old = dp[j]
                if word1[i] == word2[j]:
                    dp[j] = next_diag
                else:
                    dp[j] = 1 + min(dp[j], dp[j + 1], next_diag)
                next_diag = old
        return dp[0]
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(3^(m+n)) | O(m+n) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Empty strings reduce to all insertions or deletions.
- In tabulation, preserve the old diagonal value for replacement.

## Related
- Longest Common Subsequence
- Regular Expression Matching
