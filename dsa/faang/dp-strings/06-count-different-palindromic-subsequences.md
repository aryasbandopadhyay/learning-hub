# 06. Count Different Palindromic Subsequences

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Meta, Amazon

## Problem
Implement `countPalindromicSubsequences` for **Count Different Palindromic Subsequences**. Given a string `s`, return the number of different non-empty palindromic subsequences modulo `1_000_000_007`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `s`: string; input string.

**Output**
- A single integer.

## Constraints
- `1 <= len(s) <= 1000`
- `s` contains only `a`, `b`, `c`, and `d`

## Examples
```text
Input: s = "bccb"
Output: 6
Explanation: The palindromes are "b", "c", "bb", "cc", "bcb", and "bccb". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
The same palindrome can arise from many index sets, so duplicate handling matters. Grouping by the outer character avoids double counting. For each interval and outer letter, either inherit a smaller interval or wrap all inner palindromes.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subsequences and deduplicate palindromes.
```python
class Solution:
    def countPalindromicSubsequences(self, s: str) -> int:
        seen = set()
        n = len(s)
        for mask in range(1, 1 << n):
            t = []
            for i in range(n):
                if mask & (1 << i):
                    t.append(s[i])
            word = "".join(t)
            if word == word[::-1]:
                seen.add(word)
        return len(seen) % 1000000007
```
- **Time:** O(n*2^n) — **Space:** O(2^n)

## Approach 2 — Better
**Idea:** Use interval inclusion-exclusion and scan duplicate boundary characters.
```python
class Solution:
    def countPalindromicSubsequences(self, s):
        mod = 1000000007
        n = len(s)
        dp = [[0] * n for _ in range(n)]
        for i in range(n):
            dp[i][i] = 1
        for length in range(2, n + 1):
            for l in range(n - length + 1):
                r = l + length - 1
                if s[l] != s[r]:
                    dp[l][r] = dp[l + 1][r] + dp[l][r - 1] - dp[l + 1][r - 1]
                else:
                    lo, hi = l + 1, r - 1
                    while lo <= hi and s[lo] != s[l]:
                        lo += 1
                    while lo <= hi and s[hi] != s[l]:
                        hi -= 1
                    if lo > hi:
                        dp[l][r] = 2 * dp[l + 1][r - 1] + 2
                    elif lo == hi:
                        dp[l][r] = 2 * dp[l + 1][r - 1] + 1
                    else:
                        dp[l][r] = 2 * dp[l + 1][r - 1] - dp[lo + 1][hi - 1]
                dp[l][r] %= mod
        return dp[0][n - 1]
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Store counts by outer letter for every interval.
```python
class Solution:
    def countPalindromicSubsequences(self, s):
        mod = 1000000007
        n = len(s)
        chars = "abcd"
        dp = [[[0] * 4 for _ in range(n)] for _ in range(n)]
        for i, ch in enumerate(s):
            dp[i][i][chars.index(ch)] = 1
        for length in range(2, n + 1):
            for l in range(n - length + 1):
                r = l + length - 1
                for k, ch in enumerate(chars):
                    if s[l] == ch and s[r] == ch:
                        dp[l][r][k] = 2 + sum(dp[l + 1][r - 1])
                    elif s[l] == ch:
                        dp[l][r][k] = dp[l][r - 1][k]
                    elif s[r] == ch:
                        dp[l][r][k] = dp[l + 1][r][k]
                    else:
                        dp[l][r][k] = dp[l + 1][r - 1][k]
                    dp[l][r][k] %= mod
        return sum(dp[0][n - 1]) % mod
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n*2^n) | O(2^n) |
| Better | O(n^3) | O(n^2) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- Count distinct strings, not index selections.
- Mod after subtraction.
- The optimized DP uses the four-letter constraint.

## Related
- Longest Palindromic Subsequence
- Palindromic Substrings
