# 06. Palindromic Substrings

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given a string `s`, count all contiguous substrings that are palindromes. Substrings with different
start or end positions count separately, even if they contain the same characters.

**Input**
- `s`: a string.

**Output**
- An integer: the number of palindromic substrings in `s`.

## Constraints
- 1 <= s.length <= 1000
- `s` consists of lowercase English letters.

## Examples
```text
Input: s = "aaa"
Output: 6
Explanation: The palindromic substrings are three single letters, two occurrences of `"aa"`, and one `"aaa"`, for a total of `6`.
```

## Understanding & Intuition
Use the same palindrome state: `s[l:r+1]` is valid if `s[l] == s[r]` and the inside is valid. Count every range satisfying that recurrence. A 1-D DP row can represent the next inner row.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively scan all substrings and recursively check palindrome status.
```python
class Solution:
    def countSubstrings(self, s: str) -> int:
        def is_pal(l: int, r: int) -> bool:
            if l >= r:
                return True
            return s[l] == s[r] and is_pal(l + 1, r - 1)

        def scan(l: int, r: int) -> int:
            if l == len(s):
                return 0
            if r == len(s):
                return scan(l + 1, l + 1)
            return (1 if is_pal(l, r) else 0) + scan(l, r + 1)

        return scan(0, 0)
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize palindrome checks while enumerating all substrings.
```python
class Solution:
    def countSubstrings(self, s: str) -> int:
        memo = {}

        def is_pal(l: int, r: int) -> bool:
            if l >= r:
                return True
            if (l, r) not in memo:
                memo[(l, r)] = s[l] == s[r] and is_pal(l + 1, r - 1)
            return memo[(l, r)]

        count = 0
        for l in range(len(s)):
            for r in range(l, len(s)):
                if is_pal(l, r):
                    count += 1
        return count
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Tabulate palindromes with one previous row.
```python
class Solution:
    def countSubstrings(self, s: str) -> int:
        n = len(s)
        dp = [False] * n
        count = 0

        for l in range(n - 1, -1, -1):
            new_dp = [False] * n
            for r in range(l, n):
                new_dp[r] = s[l] == s[r] and (r - l < 2 or dp[r - 1])
                if new_dp[r]:
                    count += 1
            dp = new_dp

        return count
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Same text at different indices counts separately.
- Count all single-character substrings.

## Related
- Longest Palindromic Substring
- Palindrome Partitioning
