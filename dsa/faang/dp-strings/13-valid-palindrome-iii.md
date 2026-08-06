# 13. Valid Palindrome III

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Meta, Amazon

## Problem
Given a string `s` and integer `k`, return whether `s` can become a palindrome after deleting at most `k` characters. Constraints: `1 <= len(s) <= 1000`, `0 <= k <= len(s)`; lowercase English letters.

## Examples
```text
Input: s = "abcdeca", k = 2
Output: True
Explanation: Delete 'b' and 'e' to get "acdca".
```

## Understanding & Intuition
Equal endpoints can stay without cost. Unequal endpoints force deleting one of them. The minimum deletions for the whole interval is compared with `k`.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively branch on which mismatching endpoint to delete.
```python
class Solution:
    def isValidPalindrome(self, s: str, k: int) -> bool:
        def dfs(l, r):
            if l >= r:
                return 0
            if s[l] == s[r]:
                return dfs(l + 1, r - 1)
            return 1 + min(dfs(l + 1, r), dfs(l, r - 1))
        return dfs(0, len(s) - 1) <= k
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize minimum deletions per interval.
```python
class Solution:
    def isValidPalindrome(self, s, k):
        from functools import lru_cache
        @lru_cache(None)
        def dfs(l, r):
            if l >= r:
                return 0
            if s[l] == s[r]:
                return dfs(l + 1, r - 1)
            return 1 + min(dfs(l + 1, r), dfs(l, r - 1))
        return dfs(0, len(s) - 1) <= k
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Compute longest palindromic subsequence with one-row DP and delete everything outside it.
```python
class Solution:
    def isValidPalindrome(self, s, k):
        n = len(s)
        dp = [1] * n
        for l in range(n - 2, -1, -1):
            prev = 0
            for r in range(l + 1, n):
                old = dp[r]
                if s[l] == s[r]:
                    dp[r] = prev + 2
                else:
                    dp[r] = max(dp[r], dp[r - 1])
                prev = old
        return n - dp[-1] <= k
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- `k = 0` requires the original string to be a palindrome.
- At most `k` deletions are allowed, not exactly `k`.
- The LPS condition is `len(s) - LPS <= k`.

## Related
- Longest Palindromic Subsequence
- Minimum Insertion Steps to Make a String Palindrome
