# 01. Valid Parentheses

- **Difficulty:** Easy
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a string `s` containing only bracket characters, determine whether it is a valid bracket sequence. Every opening bracket must be closed by the same type in the correct nested order.

**Input**
- `s`: a string containing only `(`, `)`, `[`, `]`, `{`, and `}`.

**Output**
- `True` if the sequence is valid; otherwise `False`.

## Constraints
- `1 <= s.length <= 10^4`
- `s` contains only bracket characters.

## Examples
```text
Input: s = "()[]{}"
Output: True
Explanation: Each opening bracket is immediately closed by the matching type, and no closing bracket appears out of order.
```

## Understanding & Intuition
The most recent unmatched opening bracket must be closed first. That last-in-first-out rule is exactly a stack. A mismatch or an early closing bracket makes the string invalid.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly remove adjacent valid pairs until no more pairs can be removed.
```python
class Solution:
    def isValid(self, s: str) -> bool:
        # Keep deleting simple valid pairs; remaining text means invalid nesting.
        changed = True
        while changed:
            old = s
            s = s.replace("()", "").replace("[]", "").replace("{}", "")
            changed = s != old
        return s == ""
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack and explicit conditional checks for matching closing brackets.
```python
class Solution:
    def isValid(self, s: str) -> bool:
        stack = []
        for ch in s:
            if ch in "([{":
                stack.append(ch)
            else:
                if not stack:
                    return False
                top = stack.pop()
                # The popped opener must match the current closer.
                if (ch == ")" and top != "(") or (ch == "]" and top != "[") or (ch == "}" and top != "{"):
                    return False
        return not stack
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a closer-to-opener map to make the same stack logic compact and less error-prone.
```python
class Solution:
    def isValid(self, s: str) -> bool:
        pairs = {")": "(", "]": "[", "}": "{"}
        stack = []
        for ch in s:
            if ch in pairs:
                # Pop a sentinel if there is no opener, forcing a mismatch.
                if not stack or stack.pop() != pairs[ch]:
                    return False
            else:
                stack.append(ch)
        return len(stack) == 0
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A closing bracket before any opening bracket is invalid.
- Different bracket types cannot cross, such as `"([)]"`.
- The stack must be empty at the end.

## Related
- Min Stack
- Generate Parentheses

