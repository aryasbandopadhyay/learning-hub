# 02. Minimum Insertion Steps to Make a String Palindrome

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Amazon, Microsoft

## Problem
Implement `minInsertions` for **Minimum Insertion Steps to Make a String Palindrome**. Given a string `s`, return the minimum number of characters to insert anywhere so that `s` becomes a palindrome.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `s`: string; input string.

**Output**
- A single integer.

## Constraints
- `1 <= len(s) <= 500`
- lowercase English letters

## Examples
```text
Input: s = "mbadm"
Output: 2
Explanation: One optimal palindrome is "mbdadbm". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Equal endpoints need no new character. If endpoints differ, insert a matching partner for one side and solve the smaller interval. The minimum interval cost is the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try inserting for the left side or the right side.
```python
class Solution:
    def minInsertions(self, s: str) -> int:
        def dfs(l, r):
            if l >= r:
                return 0
            if s[l] == s[r]:
                return dfs(l + 1, r - 1)
            return 1 + min(dfs(l + 1, r), dfs(l, r - 1))
        return dfs(0, len(s) - 1)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Cache interval costs.
```python
class Solution:
    def minInsertions(self, s):
        from functools import lru_cache
        @lru_cache(None)
        def dfs(l, r):
            if l >= r:
                return 0
            if s[l] == s[r]:
                return dfs(l + 1, r - 1)
            return 1 + min(dfs(l + 1, r), dfs(l, r - 1))
        return dfs(0, len(s) - 1)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Find the longest palindromic subsequence; all other characters need partners inserted.
```python
class Solution:
    def minInsertions(self, s):
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
        return n - dp[-1]
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Palindromes need `0` insertions.
- The count equals `len(s) - LPS`.
- Preserve diagonal state in compressed DP.

## Related
- Longest Palindromic Subsequence
- Valid Palindrome III
