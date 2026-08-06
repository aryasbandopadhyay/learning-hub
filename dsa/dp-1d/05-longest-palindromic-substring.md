# 05. Longest Palindromic Substring

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given a string `s`, return the longest palindromic substring. Constraints: `1 <= s.length <= 1000`, `s` contains only digits and English letters.

## Examples
```text
Input: s = "babad"
Output: "bab"
Explanation: "aba" is also valid.
```

## Understanding & Intuition
A substring `s[l:r+1]` is a palindrome when its ends match and the inside substring is a palindrome. The DP state is `is_pal(l, r)`. Scanning all ranges and keeping the longest valid one gives the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Generate substrings recursively and check each one by recursive two-pointer comparison.
```python
class Solution:
    def longestPalindrome(self, s: str) -> str:
        best = ""

        def is_pal(l: int, r: int) -> bool:
            if l >= r:
                return True
            return s[l] == s[r] and is_pal(l + 1, r - 1)

        def scan(l: int, r: int) -> None:
            nonlocal best
            if l == len(s):
                return
            if r == len(s):
                scan(l + 1, l + 1)
                return
            if r - l + 1 > len(best) and is_pal(l, r):
                best = s[l:r + 1]
            scan(l, r + 1)

        scan(0, 0)
        return best
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize whether each `(l, r)` range is a palindrome.
```python
class Solution:
    def longestPalindrome(self, s: str) -> str:
        memo = {}

        def is_pal(l: int, r: int) -> bool:
            if l >= r:
                return True
            if (l, r) not in memo:
                memo[(l, r)] = s[l] == s[r] and is_pal(l + 1, r - 1)
            return memo[(l, r)]

        best = s[0]
        for l in range(len(s)):
            for r in range(l, len(s)):
                if r - l + 1 > len(best) and is_pal(l, r):
                    best = s[l:r + 1]
        return best
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Use 1-D tabulation where `dp[r]` means `s[l:r+1]` is palindrome for the current `l`.
```python
class Solution:
    def longestPalindrome(self, s: str) -> str:
        n = len(s)
        dp = [False] * n
        start, best_len = 0, 1

        for l in range(n - 1, -1, -1):
            new_dp = [False] * n
            for r in range(l, n):
                new_dp[r] = s[l] == s[r] and (r - l < 2 or dp[r - 1])
                if new_dp[r] and r - l + 1 > best_len:
                    start, best_len = l, r - l + 1
            dp = new_dp

        return s[start:start + best_len]
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Multiple longest answers may be valid.
- Length 1 and 2 substrings need special handling.

## Related
- Palindromic Substrings
- Longest Palindromic Subsequence
