# 09. Palindrome Number

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given an integer `x`, return whether it reads the same forward and backward. Constraints: `-2^31 <= x <= 2^31 - 1`.

## Examples
```text
Input: x = 121
Output: True
Explanation: 121 reads the same from both directions.
```

## Understanding & Intuition
Negative numbers are not palindromes because of the minus sign. String reversal is simple. The optimal numeric method reverses only half the digits to avoid overflow concerns in fixed-width languages.

## Approach 1 — Naive / Brute Force
**Idea:** Convert to a string and compare with its reverse.
```python
class Solution:
    def isPalindrome(self, x: int) -> bool:
        s = str(x)
        return s == s[::-1]
```
- **Time:** O(d) — **Space:** O(d)

## Approach 2 — Better
**Idea:** Reverse all digits numerically and compare with the original.
```python
class Solution:
    def isPalindrome(self, x: int) -> bool:
        if x < 0:
            return False
        original, rev = x, 0
        while x:
            rev = rev * 10 + x % 10
            x //= 10
        return rev == original
```
- **Time:** O(d) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Reverse only the second half and compare halves.
```python
class Solution:
    def isPalindrome(self, x: int) -> bool:
        if x < 0 or (x % 10 == 0 and x != 0):
            return False
        rev_half = 0
        while x > rev_half:
            rev_half = rev_half * 10 + x % 10
            x //= 10
        # For odd digit counts, drop the middle digit from rev_half.
        return x == rev_half or x == rev_half // 10
```
- **Time:** O(d) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(d) | O(d) |
| Better | O(d) | O(1) |
| Optimal | O(d) | O(1) |

## Edge Cases & Pitfalls
- Negative numbers are false.
- Positive numbers ending in zero are false except `0`.

## Related
- Reverse Integer
- Valid Palindrome
