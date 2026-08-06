# 11. Backspace String Compare

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Google, Meta, Amazon, Microsoft

## Problem
Given strings `s` and `t`, where `#` means backspace, return whether both strings are equal after applying all backspaces. Constraints: `1 <= len(s), len(t) <= 200`.

## Examples
```text
Input: s = "ab#c", t = "ad#c"
Output: True
Explanation: Both strings become "ac".
```

## Understanding & Intuition
A `#` removes the nearest previous live character. Simulating with stacks is simple. The optimal approach scans backward, skipping characters that would be deleted.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly apply the first visible backspace by rebuilding strings.
```python
class Solution:
    def backspaceCompare(self, s: str, t: str) -> bool:
        def reduce_text(text: str) -> str:
            chars = list(text)
            changed = True
            while changed:
                changed = False
                for i, ch in enumerate(chars):
                    if ch == "#":
                        start = i - 1 if i > 0 else i
                        del chars[start:i + 1]
                        changed = True
                        break
            return "".join(chars)

        return reduce_text(s) == reduce_text(t)
```
- **Time:** O(n² + m²) — **Space:** O(n + m)

## Approach 2 — Better
**Idea:** Use a stack for each string to process characters once.
```python
class Solution:
    def backspaceCompare(self, s: str, t: str) -> bool:
        def build(text: str) -> str:
            stack = []
            for ch in text:
                if ch == "#":
                    if stack:
                        stack.pop()
                else:
                    stack.append(ch)
            return "".join(stack)

        return build(s) == build(t)
```
- **Time:** O(n + m) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Walk both strings backward, skipping deleted characters without constructing final strings.
```python
class Solution:
    def backspaceCompare(self, s: str, t: str) -> bool:
        def next_valid(text: str, i: int) -> int:
            skip = 0
            while i >= 0:
                if text[i] == "#":
                    skip += 1
                    i -= 1
                elif skip:
                    skip -= 1
                    i -= 1
                else:
                    break
            return i

        i, j = len(s) - 1, len(t) - 1
        while i >= 0 or j >= 0:
            i = next_valid(s, i)
            j = next_valid(t, j)
            if i < 0 or j < 0:
                return i == j
            if s[i] != t[j]:
                return False
            i -= 1
            j -= 1
        return True
```
- **Time:** O(n + m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n² + m²) | O(n + m) |
| Better | O(n + m) | O(n + m) |
| Optimal | O(n + m) | O(1) |

## Edge Cases & Pitfalls
- Backspacing an empty string does nothing.
- Multiple consecutive `#` characters stack their effects.
- Continue until both strings are exhausted.

## Related
- Valid Palindrome
- Remove Element
