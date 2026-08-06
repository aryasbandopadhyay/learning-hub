# 03. Longest Common Substring

- **Difficulty:** Medium
- **Pattern:** DP on strings
- **Asked at:** Amazon, Bloomberg, Microsoft

## Problem
Given strings `text1` and `text2`, return the length of their longest common substring. A substring is contiguous in both strings. Constraints: `0 <= len(text1), len(text2) <= 1000`; lowercase English letters.

## Examples
```text
Input: text1 = "abcdxyz", text2 = "xyzabcd"
Output: 4
Explanation: The longest common substring is "abcd".
```

## Understanding & Intuition
A mismatch resets a contiguous match to zero. If characters match, the best suffix length extends the previous diagonal. The maximum suffix length over all pairs is the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Start at every pair of indices and extend while equal.
```python
class Solution:
    def longestCommonSubstring(self, text1: str, text2: str) -> int:
        best = 0
        for i in range(len(text1)):
            for j in range(len(text2)):
                k = 0
                while i + k < len(text1) and j + k < len(text2) and text1[i + k] == text2[j + k]:
                    k += 1
                best = max(best, k)
        return best
```
- **Time:** O(n*m*min(n,m)) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Store all common-suffix lengths in a table.
```python
class Solution:
    def longestCommonSubstring(self, text1, text2):
        n, m = len(text1), len(text2)
        dp = [[0] * (m + 1) for _ in range(n + 1)]
        best = 0
        for i in range(1, n + 1):
            for j in range(1, m + 1):
                if text1[i - 1] == text2[j - 1]:
                    dp[i][j] = dp[i - 1][j - 1] + 1
                    best = max(best, dp[i][j])
        return best
```
- **Time:** O(n*m) — **Space:** O(n*m)

## Approach 3 — Optimal
**Idea:** Keep only the previous row.
```python
class Solution:
    def longestCommonSubstring(self, text1, text2):
        if len(text2) > len(text1):
            text1, text2 = text2, text1
        prev = [0] * (len(text2) + 1)
        best = 0
        for a in text1:
            curr = [0] * (len(text2) + 1)
            for j, b in enumerate(text2, 1):
                if a == b:
                    curr[j] = prev[j - 1] + 1
                    best = max(best, curr[j])
            prev = curr
        return best
```
- **Time:** O(n*m) — **Space:** O(min(n,m))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n*m*min(n,m)) | O(1) |
| Better | O(n*m) | O(n*m) |
| Optimal | O(n*m) | O(min(n,m)) |

## Edge Cases & Pitfalls
- Empty input returns `0`.
- Do not confuse substring with subsequence.
- Reset on mismatch.

## Related
- Longest Common Subsequence
- Shortest Common Supersequence
