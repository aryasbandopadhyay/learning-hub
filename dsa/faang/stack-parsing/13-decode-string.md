# 13. Decode String

- **Difficulty:** Medium
- **Pattern:** Stack Parsing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an encoded string `s`, return its decoded string. The encoding rule is `k[encoded_string]`, where `encoded_string` inside brackets is repeated exactly `k` times. Inputs are valid, `k` is a positive integer, and the decoded output fits in memory.

## Examples
```text
Input: s = "3[a2[c]]"
Output: "accaccacc"
Explanation: Decode "a2[c]" as "acc", then repeat it three times.
```

## Understanding & Intuition
Brackets create nested scopes, so the latest open bracket must be resolved first. Repetition counts can have multiple digits. A stack or recursion naturally models this nesting.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively parse from the current index and return when a closing bracket ends the current scope.
```python
class Solution:
    def decodeString(self, s: str) -> str:
        def parse(i):
            out = []
            while i < len(s) and s[i] != ']':
                if s[i].isdigit():
                    count = 0
                    while i < len(s) and s[i].isdigit():
                        count = count * 10 + int(s[i])
                        i += 1
                    i += 1
                    inner, i = parse(i)
                    out.append(inner * count)
                    i += 1
                else:
                    out.append(s[i])
                    i += 1
            return "".join(out), i
        decoded, _ = parse(0)
        return decoded
```
- **Time:** O(L) — **Space:** O(D + L)

## Approach 2 — Better
**Idea:** Use separate stacks for previous strings and repeat counts whenever a new bracket opens.
```python
class Solution:
    def decodeString(self, s: str) -> str:
        count_stack = []
        string_stack = []
        current = []
        count = 0
        for ch in s:
            if ch.isdigit():
                count = count * 10 + int(ch)
            elif ch == '[':
                count_stack.append(count)
                string_stack.append(current)
                current = []
                count = 0
            elif ch == ']':
                repeat = count_stack.pop()
                previous = string_stack.pop()
                current = previous + current * repeat
            else:
                current.append(ch)
        return "".join(current)
```
- **Time:** O(L) — **Space:** O(D + L)

## Approach 3 — Optimal
**Idea:** Keep one stack of strings, numbers, and brackets; collapse the top segment whenever `]` appears.
```python
class Solution:
    def decodeString(self, s: str) -> str:
        stack = []
        for ch in s:
            if ch != ']':
                stack.append(ch)
            else:
                segment = []
                while stack and stack[-1] != '[':
                    segment.append(stack.pop())
                stack.pop()
                multiplier = 0
                base = 1
                while stack and stack[-1].isdigit():
                    multiplier += (ord(stack.pop()) - ord('0')) * base
                    base *= 10
                stack.append("".join(reversed(segment)) * multiplier)
        return "".join(stack)
```
- **Time:** O(L) — **Space:** O(L)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(L) | O(D + L) |
| Better | O(L) | O(D + L) |
| Optimal | O(L) | O(L) |

## Edge Cases & Pitfalls
- Repeat counts can have more than one digit.
- Decode nested brackets from the inside out.
- Append plain letters that appear outside brackets unchanged.

## Related
- Basic Calculator
- Valid Parentheses
