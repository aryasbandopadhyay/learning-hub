# 05. Minimum Add to Make Parentheses Valid

- **Difficulty:** Medium
- **Pattern:** advanced strings
- **Asked at:** Meta, Amazon, Google

## Problem
Given a string `s` containing only `(` and `)`, return the minimum number of parentheses that must be added to make it valid.

**Input**
- `s`: a `str`; the input string.

**Output**
- A `int`. Return the minimum number of parentheses that must be added to make it valid.

## Constraints
- `0 <= len(s) <= 100000`.

## Examples
```text
Input: s = "()))(("
Output: 4
Explanation: One opening parenthesis fixes the extra close, and three closing parentheses fix the remaining opens.
```

## Understanding & Intuition
A valid prefix never has more closing parentheses than opening parentheses. Every unmatched close requires one inserted open. Any openings left at the end require matching closes.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate cancellation with a stack of unmatched parentheses.
```python
class Solution:
    def minAddToMakeValid(self, s: str) -> int:
        stack = []
        for ch in s:
            if ch == "(":
                stack.append(ch)
            elif stack and stack[-1] == "(":
                stack.pop()
            else:
                stack.append(ch)
        return len(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count unmatched opens and additions needed for unmatched closes.
```python
class Solution:
    def minAddToMakeValid(self, s):
        opens = 0
        additions = 0
        for ch in s:
            if ch == "(":
                opens += 1
            elif opens:
                opens -= 1
            else:
                additions += 1
        return additions + opens
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Track balance; whenever it goes negative, insert an opening parenthesis and reset.
```python
class Solution:
    def minAddToMakeValid(self, s):
        balance = 0
        needed = 0
        for ch in s:
            balance += 1 if ch == "(" else -1
            if balance < 0:
                needed += 1
                balance = 0
        return needed + balance
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A leading `)` always needs an inserted `(`.
- Remaining positive balance needs that many `)` insertions.
- Empty input returns `0`.

## Related
- Valid Parentheses
- Longest Valid Parentheses
