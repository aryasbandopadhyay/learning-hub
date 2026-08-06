# 11. Decode String

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an encoded string with the rule `k[encoded_string]`, return the decoded string. `k` is a positive integer, brackets are well formed, and decoded output length is reasonable. Constraints: `1 <= len(s) <= 30`.

## Examples
```text
Input: s = "3[a2[c]]"
Output: "accaccacc"
Explanation: "a2[c]" becomes "acc", repeated three times.
```

## Understanding & Intuition
Nested brackets require remembering the previous string and repeat count. A stack stores those states when `[` starts a nested segment. When `]` appears, the current segment is repeated and appended to the previous one.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly decode the innermost bracketed expression in the string.
```python
class Solution:
    def decodeString(self, s: str) -> str:
        while "]" in s:
            close = s.index("]")
            open_idx = s.rfind("[", 0, close)
            k_start = open_idx - 1
            while k_start >= 0 and s[k_start].isdigit():
                k_start -= 1
            count = int(s[k_start + 1:open_idx])
            expanded = s[open_idx + 1:close] * count
            # Replace one innermost encoded part.
            s = s[:k_start + 1] + expanded + s[close + 1:]
        return s
```
- **Time:** O(n * decoded_length) — **Space:** O(decoded_length)

## Approach 2 — Better
**Idea:** Push characters and expand when a closing bracket is seen.
```python
class Solution:
    def decodeString(self, s: str) -> str:
        stack = []
        for ch in s:
            if ch != "]":
                stack.append(ch)
                continue
            part = []
            while stack[-1] != "[":
                part.append(stack.pop())
            stack.pop()
            digits = []
            while stack and stack[-1].isdigit():
                digits.append(stack.pop())
            count = int("".join(reversed(digits)))
            stack.extend(reversed(part * count))
        return "".join(stack)
```
- **Time:** O(decoded_length) — **Space:** O(decoded_length)

## Approach 3 — Optimal
**Idea:** Store `(previous_string, repeat_count)` frames and build the current segment directly.
```python
class Solution:
    def decodeString(self, s: str) -> str:
        stack = []
        current = []
        number = 0
        for ch in s:
            if ch.isdigit():
                number = number * 10 + int(ch)
            elif ch == "[":
                stack.append((current, number))
                current = []
                number = 0
            elif ch == "]":
                prev, count = stack.pop()
                # Complete this frame and restore the outer string.
                current = prev + current * count
            else:
                current.append(ch)
        return "".join(current)
```
- **Time:** O(decoded_length) — **Space:** O(decoded_length)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * decoded_length) | O(decoded_length) |
| Better | O(decoded_length) | O(decoded_length) |
| Optimal | O(decoded_length) | O(decoded_length) |

## Edge Cases & Pitfalls
- Repeat counts may have multiple digits.
- Nested frames must restore the previous string, not overwrite it.
- Only expand after seeing the matching `]`.

## Related
- Generate Parentheses
- Basic Calculator

