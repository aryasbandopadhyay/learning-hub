# 13. Longest Palindromic Subsequence

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Microsoft

## Problem
You are given a string `s`.

Return the length of the longest subsequence that reads the same forward and backward. A subsequence may delete characters but must preserve the order of the remaining characters.

**Input**
- `s`: a string.

**Output**
- The length of the longest palindromic subsequence.

## Constraints
- `1 <= s.length <= 1000`
- `s` contains only lowercase English letters.

## Examples
```text
Input: s = "bbbab"
Output: 4
Explanation: One longest palindromic subsequence is `bbbb`, which has length `4`.
```

## Understanding & Intuition
For any interval, matching endpoints can wrap the best answer inside. If endpoints differ, one endpoint must be excluded from an optimal subsequence.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subsequences and test whether each is a palindrome.
```python
class Solution:
    def longestPalindromeSubseq(self, s: str) -> int:
        n = len(s)
        best = 0
        def dfs(i: int, chars: list[str]) -> None:
            nonlocal best
            if i == n:
                if chars == chars[::-1]:
                    best = max(best, len(chars))
                return
            dfs(i + 1, chars)
            chars.append(s[i])
            dfs(i + 1, chars)
            chars.pop()
        dfs(0, [])
        return best
```
- **Time:** O(n2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize interval recursion on `(left, right)`.
```python
class Solution:
    def longestPalindromeSubseq(self, s: str) -> int:
        from functools import lru_cache
        @lru_cache(None)
        def dp(l: int, r: int) -> int:
            if l > r:
                return 0
            if l == r:
                return 1
            if s[l] == s[r]:
                return 2 + dp(l + 1, r - 1)
            return max(dp(l + 1, r), dp(l, r - 1))
        return dp(0, len(s) - 1)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Fill interval DP with one dimension by scanning starts backward and ends forward.
```python
class Solution:
    def longestPalindromeSubseq(self, s: str) -> int:
        n = len(s)
        dp = [0] * n
        for i in range(n - 1, -1, -1):
            dp[i] = 1
            prev = 0
            for j in range(i + 1, n):
                old = dp[j]
                if s[i] == s[j]:
                    dp[j] = prev + 2
                else:
                    dp[j] = max(dp[j], dp[j - 1])
                prev = old
        return dp[n - 1]
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n2^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- This asks for a subsequence, not a contiguous substring.
- Single characters are palindromes of length 1.

## Related
- Longest Common Subsequence
- Palindromic Substrings
