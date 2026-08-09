# 04. Shortest Common Supersequence

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Amazon, Meta

## Problem
Implement `shortestCommonSupersequence` for **Shortest Common Supersequence**. Given strings `str1` and `str2`, return a deterministic shortest string having both as subsequences. If optimal choices tie, prefer taking the next character from `str1`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

This judge compares exactly; when shortest choices tie, use the deterministic tie rule stated in the description.

**Input**
- `str1`: string; first string.
- `str2`: string; second string.

**Output**
- A string. This judge compares exactly; when shortest choices tie, use the deterministic tie rule stated in the description.

## Constraints
- `0 <= len(str1), len(str2) <= 200`
- lowercase English letters

## Examples
```text
Input: str1 = "abac", str2 = "cab"
Output: "cabac"
Explanation: It is shortest, and ties are resolved by the stated rule. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
A supersequence must include both current characters unless they are equal. DP gives the shortest remaining length from each pair of positions. Reconstruction uses the same tie rule in every approach.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively build both possible suffixes and choose the shorter, preferring `str1` on ties.
```python
class Solution:
    def shortestCommonSupersequence(self, str1: str, str2: str) -> str:
        def choose(a, b):
            return a if len(a) <= len(b) else b
        def dfs(i, j):
            if i == len(str1):
                return str2[j:]
            if j == len(str2):
                return str1[i:]
            if str1[i] == str2[j]:
                return str1[i] + dfs(i + 1, j + 1)
            return choose(str1[i] + dfs(i + 1, j), str2[j] + dfs(i, j + 1))
        return dfs(0, 0)
```
- **Time:** O(2^(n+m)*(n+m)) — **Space:** O(n+m)

## Approach 2 — Better
**Idea:** Memoize the canonical suffix string for each state.
```python
class Solution:
    def shortestCommonSupersequence(self, str1, str2):
        from functools import lru_cache
        def choose(a, b):
            return a if len(a) <= len(b) else b
        @lru_cache(None)
        def dfs(i, j):
            if i == len(str1):
                return str2[j:]
            if j == len(str2):
                return str1[i:]
            if str1[i] == str2[j]:
                return str1[i] + dfs(i + 1, j + 1)
            return choose(str1[i] + dfs(i + 1, j), str2[j] + dfs(i, j + 1))
        return dfs(0, 0)
```
- **Time:** O(n*m*(n+m)) — **Space:** O(n*m*(n+m))

## Approach 3 — Optimal
**Idea:** Store shortest lengths, then reconstruct deterministically.
```python
class Solution:
    def shortestCommonSupersequence(self, str1, str2):
        n, m = len(str1), len(str2)
        dp = [[0] * (m + 1) for _ in range(n + 1)]
        for i in range(n, -1, -1):
            for j in range(m, -1, -1):
                if i == n:
                    dp[i][j] = m - j
                elif j == m:
                    dp[i][j] = n - i
                elif str1[i] == str2[j]:
                    dp[i][j] = 1 + dp[i + 1][j + 1]
                else:
                    dp[i][j] = 1 + min(dp[i + 1][j], dp[i][j + 1])
        i = j = 0
        out = []
        while i < n and j < m:
            if str1[i] == str2[j]:
                out.append(str1[i]); i += 1; j += 1
            elif dp[i + 1][j] <= dp[i][j + 1]:
                out.append(str1[i]); i += 1
            else:
                out.append(str2[j]); j += 1
        out.append(str1[i:]); out.append(str2[j:])
        return "".join(out)
```
- **Time:** O(n*m) — **Space:** O(n*m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(n+m)*(n+m)) | O(n+m) |
| Better | O(n*m*(n+m)) | O(n*m*(n+m)) |
| Optimal | O(n*m) | O(n*m) |

## Edge Cases & Pitfalls
- Empty strings return the other string.
- Tie-breaking must match across approaches.
- The output is a string, not the length.

## Related
- Longest Common Subsequence
- Longest Common Substring
