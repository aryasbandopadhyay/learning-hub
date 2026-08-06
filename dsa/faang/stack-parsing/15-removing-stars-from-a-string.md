# 15. Removing Stars From a String

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta

## Problem
Given a string `s` containing lowercase letters and stars, repeatedly choose a star, remove the closest non-star character to its left, and remove the star itself. The input is valid, so every star has a character to remove. Return the final string.

## Examples
```text
Input: s = "leet**cod*e"
Output: "lecoe"
Explanation: The stars remove the two t's and then the d.
```

## Understanding & Intuition
A star always deletes the nearest surviving character on its left. That is exactly the last character kept so far. A stack stores the current surviving prefix and makes each deletion local.

## Approach 1 — Naive / Brute Force
**Idea:** Store characters in a list and, for each star, scan left to delete the closest still-present character.
```python
class Solution:
    def removeStars(self, s: str) -> str:
        chars = list(s)
        for i, ch in enumerate(chars):
            if ch == '*':
                j = i - 1
                while j >= 0 and chars[j] == '':
                    j -= 1
                chars[j] = ''
                chars[i] = ''
        return "".join(chars)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack list to append letters and pop the most recent one when a star appears.
```python
class Solution:
    def removeStars(self, s: str) -> str:
        stack = []
        for ch in s:
            if ch == '*':
                stack.pop()
            else:
                stack.append(ch)
        return "".join(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Traverse from right to left, count pending stars, and keep only letters not removed by a star to their right.
```python
class Solution:
    def removeStars(self, s: str) -> str:
        skip = 0
        kept = []
        for ch in reversed(s):
            if ch == '*':
                skip += 1
            elif skip:
                skip -= 1
            else:
                kept.append(ch)
        return "".join(reversed(kept))
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Every star is guaranteed to have a removable character on its left.
- Consecutive stars remove consecutive previously kept characters.
- The final string can be empty.

## Related
- Backspace String Compare
- Remove All Adjacent Duplicates In String
