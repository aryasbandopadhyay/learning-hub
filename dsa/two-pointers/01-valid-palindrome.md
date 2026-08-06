# 01. Valid Palindrome

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Meta, Amazon, Microsoft, Google

## Problem
Given a string `s`, return `True` if it is a palindrome after converting uppercase letters to lowercase and removing all non-alphanumeric characters. Constraints: `1 <= len(s) <= 2 * 10^5`.

## Examples
```text
Input: s = "A man, a plan, a canal: Panama"
Output: True
Explanation: After cleaning, "amanaplanacanalpanama" reads the same forward and backward.
```

## Understanding & Intuition
Only letters and digits matter, and case does not matter. A palindrome compares matching characters from the two ends inward. Two pointers avoid building many intermediate substrings in the optimal version.

## Approach 1 — Naive / Brute Force
**Idea:** Build a cleaned string, reverse it with slicing, and compare.
```python
class Solution:
    def isPalindrome(self, s: str) -> bool:
        cleaned = []
        for ch in s:
            # Keep only characters that affect the palindrome check.
            if ch.isalnum():
                cleaned.append(ch.lower())
        text = "".join(cleaned)
        return text == text[::-1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build the cleaned string, then compare it using two pointers.
```python
class Solution:
    def isPalindrome(self, s: str) -> bool:
        text = "".join(ch.lower() for ch in s if ch.isalnum())
        left, right = 0, len(text) - 1
        while left < right:
            # Stop as soon as a mirrored pair differs.
            if text[left] != text[right]:
                return False
            left += 1
            right -= 1
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Move pointers over the original string, skipping ignored characters in place.
```python
class Solution:
    def isPalindrome(self, s: str) -> bool:
        left, right = 0, len(s) - 1
        while left < right:
            # Skip punctuation, spaces, and symbols.
            while left < right and not s[left].isalnum():
                left += 1
            while left < right and not s[right].isalnum():
                right -= 1
            if s[left].lower() != s[right].lower():
                return False
            left += 1
            right -= 1
        return True
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Strings containing only punctuation are valid palindromes.
- Remember to lowercase before comparing.
- Do not compare punctuation as normal characters.

## Related
- Valid Palindrome II
- Reverse String
- Backspace String Compare
