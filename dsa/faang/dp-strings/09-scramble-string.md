# 09. Scramble String

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Given equal-length strings `s1` and `s2`, return whether `s2` is a scramble of `s1`. A scramble recursively splits a string into two non-empty parts and may swap children at any split. Constraints: `1 <= len(s1) == len(s2) <= 30`.

## Examples
```text
Input: s1 = "great", s2 = "rgeat"
Output: True
Explanation: Split "great" into "gr" and "eat", then swap inside "gr".
```

## Understanding & Intuition
Any scramble has a first split. The two sides either align directly or cross after a swap. An anagram check prunes impossible substring pairs.

## Approach 1 — Naive / Brute Force
**Idea:** Try all split points and both swap states recursively.
```python
class Solution:
    def isScramble(self, s1: str, s2: str) -> bool:
        if s1 == s2:
            return True
        if sorted(s1) != sorted(s2):
            return False
        n = len(s1)
        for cut in range(1, n):
            if self.isScramble(s1[:cut], s2[:cut]) and self.isScramble(s1[cut:], s2[cut:]):
                return True
            if self.isScramble(s1[:cut], s2[n - cut:]) and self.isScramble(s1[cut:], s2[:n - cut]):
                return True
        return False
```
- **Time:** O(4^n*n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize substring-pair results.
```python
class Solution:
    def isScramble(self, s1, s2):
        from functools import lru_cache
        @lru_cache(None)
        def dfs(a, b):
            if a == b:
                return True
            if sorted(a) != sorted(b):
                return False
            n = len(a)
            for cut in range(1, n):
                if dfs(a[:cut], b[:cut]) and dfs(a[cut:], b[cut:]):
                    return True
                if dfs(a[:cut], b[n - cut:]) and dfs(a[cut:], b[:n - cut]):
                    return True
            return False
        return dfs(s1, s2)
```
- **Time:** O(n^4 log n) — **Space:** O(n^3)

## Approach 3 — Optimal
**Idea:** Build bottom-up by substring length and start positions.
```python
class Solution:
    def isScramble(self, s1, s2):
        n = len(s1)
        dp = [[[False] * n for _ in range(n)] for _ in range(n + 1)]
        for i in range(n):
            for j in range(n):
                dp[1][i][j] = s1[i] == s2[j]
        for length in range(2, n + 1):
            for i in range(n - length + 1):
                for j in range(n - length + 1):
                    for cut in range(1, length):
                        if (dp[cut][i][j] and dp[length - cut][i + cut][j + cut]) or (dp[cut][i][j + length - cut] and dp[length - cut][i + cut][j]):
                            dp[length][i][j] = True
                            break
        return dp[n][0][0]
```
- **Time:** O(n^4) — **Space:** O(n^3)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(4^n*n log n) | O(n) |
| Better | O(n^4 log n) | O(n^3) |
| Optimal | O(n^4) | O(n^3) |

## Edge Cases & Pitfalls
- Character multisets must match.
- Test swapped and non-swapped cases.
- Length-one substrings are base cases.

## Related
- Wildcard Matching
- Encode String with Shortest Length
