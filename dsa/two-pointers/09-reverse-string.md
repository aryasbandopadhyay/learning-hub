# 09. Reverse String

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given an array of characters `s`, reverse the array in-place.

**Input**
- `s`: a list of single-character strings.

**Output**
- Modify `s` so its characters appear in reverse order. **This judge compares exactly** to the final list.

## Constraints
- `1 <= s.length <= 10^5`
- Each `s[i]` is a printable ASCII character.

## Examples
```text
Input: s = ["h","e","l","l","o"]
Output: ["o","l","l","e","h"]
Explanation: The characters in `hello` are reversed position by position to form `olleh`.
```

## Understanding & Intuition
Reversal pairs the first element with the last, the second with the second-last, and so on. Two pointers naturally identify each pair to swap.

## Approach 1 — Naive / Brute Force
**Idea:** Create a reversed copy and write it back into the original list.
```python
from typing import List

class Solution:
    def reverseString(self, s: List[str]) -> None:
        reversed_copy = s[::-1]
        for i, ch in enumerate(reversed_copy):
            s[i] = ch
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use Python's in-place list reverse helper.
```python
from typing import List

class Solution:
    def reverseString(self, s: List[str]) -> None:
        # list.reverse mutates the original list and returns None.
        s.reverse()
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Swap mirrored characters using two pointers.
```python
from typing import List

class Solution:
    def reverseString(self, s: List[str]) -> None:
        left, right = 0, len(s) - 1
        while left < right:
            s[left], s[right] = s[right], s[left]
            left += 1
            right -= 1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The method returns `None`; check the mutated list.
- Odd-length lists leave the middle character unchanged.
- Avoid converting to a string because the input is a mutable list.

## Related
- Valid Palindrome
- Reverse Words in a String
