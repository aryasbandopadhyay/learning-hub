# 11. Palindromic Substrings

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given a string `s`, return the number of palindromic substrings in it. Substrings with different start or end positions count separately, even if they contain the same characters. Constraints: `1 <= len(s) <= 1000`.

## Examples
```text
Input: s = "aaa"
Output: 6
Explanation: The palindromic substrings are "a", "a", "a", "aa", "aa", and "aaa".
```

## Understanding & Intuition
Every single character is a palindrome, and longer palindromes expand around centers. Brute force checks every substring. Dynamic programming remembers inner palindrome results, while center expansion counts directly.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all substrings and test whether each equals its reverse.
```python
class Solution:
    def countSubstrings(self, s: str) -> int:
        count = 0
        for left in range(len(s)):
            for right in range(left, len(s)):
                sub = s[left:right + 1]
                if sub == sub[::-1]:
                    count += 1
        return count
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use DP where `dp[left][right]` is true if the substring is a palindrome.
```python
class Solution:
    def countSubstrings(self, s: str) -> int:
        n = len(s)
        dp = [[False] * n for _ in range(n)]
        count = 0
        for length in range(1, n + 1):
            for left in range(n - length + 1):
                right = left + length - 1
                if s[left] == s[right] and (length <= 2 or dp[left + 1][right - 1]):
                    dp[left][right] = True
                    count += 1
        return count
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Expand around each possible odd and even center and count successful expansions.
```python
class Solution:
    def countSubstrings(self, s: str) -> int:
        def expand(left: int, right: int) -> int:
            total = 0
            while left >= 0 and right < len(s) and s[left] == s[right]:
                total += 1
                left -= 1
                right += 1
            return total

        answer = 0
        for center in range(len(s)):
            answer += expand(center, center)
            answer += expand(center, center + 1)
        return answer
```
- **Time:** O(n^2) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(1) |

## Edge Cases & Pitfalls
- Count substrings by position, not unique values.
- Even-length centers are between adjacent characters.
- Repeated characters can create many palindromes.

## Related
- Longest Palindromic Substring
- Manacher's Algorithm
- Dynamic Programming
