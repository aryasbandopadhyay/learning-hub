# 01. Valid Palindrome

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given a string `s`, return whether it is a palindrome after keeping only alphanumeric characters and comparing letters case-insensitively. Spaces, punctuation, and symbols are ignored; an empty normalized string is valid.

**Input**
- `s`: a string containing printable characters.

**Output**
- A boolean: `True` if the normalized string reads the same forward and backward, otherwise `False`.

## Constraints
- `1 <= s.length <= 2 * 10^5`
- `s` may contain letters, digits, spaces, punctuation, and symbols.

## Examples
```text
Input: s = "A man, a plan, a canal: Panama"
Output: true
Explanation: The normalized text is `amanaplanacanalpanama`, which is identical in both directions.
```

## Understanding & Intuition
Only letters and digits matter, and case should not matter. The core question is whether the normalized sequence is symmetric. We can build that sequence directly or compare from both ends while skipping irrelevant characters.

## Approach 1 — Naive / Brute Force
**Idea:** Build a filtered lowercase list, reverse it, and compare.
```python
class Solution:
    def isPalindrome(self, s: str) -> bool:
        # Keep only comparable characters.
        cleaned = []
        for ch in s:
            if ch.isalnum():
                cleaned.append(ch.lower())
        return cleaned == cleaned[::-1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build the normalized string once, then compare mirrored positions without allocating a reversed copy.
```python
class Solution:
    def isPalindrome(self, s: str) -> bool:
        cleaned = ''.join(ch.lower() for ch in s if ch.isalnum())
        left, right = 0, len(cleaned) - 1
        while left < right:
            if cleaned[left] != cleaned[right]:
                return False
            left += 1
            right -= 1
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use two pointers on the original string and skip non-alphanumeric characters in place.
```python
class Solution:
    def isPalindrome(self, s: str) -> bool:
        left, right = 0, len(s) - 1
        while left < right:
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
- Strings with only punctuation are valid palindromes after filtering.
- Digits are alphanumeric and must be compared.
- Always lowercase before comparing letters.

## Related
- Two Pointers
- Valid Palindrome II
- Reverse String
