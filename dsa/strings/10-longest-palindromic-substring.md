# 10. Longest Palindromic Substring

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Amazon, Meta, Google, Microsoft

## Problem
Given a string `s`, return its longest contiguous palindromic substring. If multiple longest palindromes exist, return the leftmost one expected by this judge.

**Input**
- `s`: the input string.

**Output**
- The longest palindromic substring. This judge compares exactly; ties should return the earliest longest palindrome.

## Constraints
- `1 <= s.length <= 1000`
- `s` contains digits and English letters.

## Examples
```text
Input: s = "babad"
Output: "bab"
Explanation: `bab` is a length-three palindrome; `aba` is another, but this judge expects the leftmost longest one.
```

## Understanding & Intuition
A palindrome mirrors around a center. Brute force checks every substring, but expanding from each possible center avoids rechecking invalid ranges. Manacher's algorithm optimizes further by reusing palindrome radii.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate substrings from longest to shortest and test each for palindrome.
```python
class Solution:
    def longestPalindrome(self, s: str) -> str:
        n = len(s)
        for length in range(n, 0, -1):
            for start in range(n - length + 1):
                sub = s[start:start + length]
                if sub == sub[::-1]:
                    return sub
        return ""
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Expand around every odd and even center and keep the longest result.
```python
class Solution:
    def longestPalindrome(self, s: str) -> str:
        best_left = best_right = 0

        def expand(left: int, right: int) -> None:
            nonlocal best_left, best_right
            while left >= 0 and right < len(s) and s[left] == s[right]:
                left -= 1
                right += 1
            if right - left - 1 > best_right - best_left + 1:
                best_left, best_right = left + 1, right - 1

        for center in range(len(s)):
            expand(center, center)
            expand(center, center + 1)
        return s[best_left:best_right + 1]
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use Manacher's algorithm on a transformed string to find all palindrome radii in linear time.
```python
class Solution:
    def longestPalindrome(self, s: str) -> str:
        t = "^#" + "#".join(s) + "#$"
        radii = [0] * len(t)
        center = right = 0
        best_center = best_radius = 0
        for i in range(1, len(t) - 1):
            mirror = 2 * center - i
            if i < right:
                radii[i] = min(right - i, radii[mirror])
            while t[i + radii[i] + 1] == t[i - radii[i] - 1]:
                radii[i] += 1
            if i + radii[i] > right:
                center, right = i, i + radii[i]
            if radii[i] > best_radius:
                best_center, best_radius = i, radii[i]
        start = (best_center - best_radius) // 2
        return s[start:start + best_radius]
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Even-length palindromes need centers between characters.
- Single-character strings are palindromes.
- In Manacher's algorithm, sentinels prevent bounds checks.

## Related
- Palindromic Substrings
- Manacher's Algorithm
- Dynamic Programming
