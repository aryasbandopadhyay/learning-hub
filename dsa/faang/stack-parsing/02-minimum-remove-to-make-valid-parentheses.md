# 02. Minimum Remove to Make Valid Parentheses

- **Difficulty:** Medium
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Meta, Amazon, Google

## Problem
Given a string `s` containing lowercase letters and parentheses, remove the minimum number of parentheses so the result is valid. Return the canonical result obtained by discarding every unmatched `')'` during a left-to-right scan and discarding every still-unmatched `'('` at the end. Letters must stay in their original order.

**Input**
- `s`: a `str`; the input string.

**Output**
- A `str`. Return the canonical result obtained by discarding every unmatched `')'` during a left-to-right scan and discarding every still-unmatched `'('` at the end.

## Constraints
- `1 <= len(s) <= 10^5`.

## Examples
```text
Input: s = "lee(t(c)o)de)"
Output: "lee(t(c)o)de"
Explanation: The final unmatched closing parenthesis is removed.
```

## Understanding & Intuition
A valid prefix can never have more closing than opening parentheses. A stack of open-parenthesis indices identifies exactly which opens are unmatched after the scan. Marking invalid indices gives a deterministic minimum-removal answer.

## Approach 1 — Naive / Brute Force
**Idea:** For each parenthesis, decide with a stack whether it can be kept; then rebuild the string without marked positions.
```python
class Solution:
    def minRemoveToMakeValid(self, s: str) -> str:
        stack = []
        remove = set()
        for i, ch in enumerate(s):
            if ch == '(':
                stack.append(i)
            elif ch == ')':
                if stack:
                    stack.pop()
                else:
                    remove.add(i)
        remove.update(stack)
        return ''.join(ch for i, ch in enumerate(s) if i not in remove)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** First skip invalid closings while counting balance, then scan backward to skip the unmatched openings left over.
```python
class Solution:
    def minRemoveToMakeValid(self, s: str) -> str:
        first = []
        balance = 0
        for ch in s:
            if ch == '(':
                balance += 1
                first.append(ch)
            elif ch == ')':
                if balance:
                    balance -= 1
                    first.append(ch)
            else:
                first.append(ch)
        second = []
        opens_to_skip = balance
        for ch in reversed(first):
            if ch == '(' and opens_to_skip:
                opens_to_skip -= 1
            else:
                second.append(ch)
        return ''.join(reversed(second))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Build the result in a list and keep stack positions of unmatched opens inside that result; delete those positions at the end.
```python
class Solution:
    def minRemoveToMakeValid(self, s: str) -> str:
        out = []
        opens = []
        for ch in s:
            if ch == '(':
                opens.append(len(out))
                out.append(ch)
            elif ch == ')':
                if opens:
                    opens.pop()
                    out.append(ch)
            else:
                out.append(ch)
        bad = set(opens)
        return ''.join(ch for i, ch in enumerate(out) if i not in bad)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The empty string is valid after removals.
- Letters do not affect balance and must not be reordered.
- Removing leftmost unmatched closings and remaining unmatched openings makes output deterministic.

## Related
- Valid Parentheses
- Remove Invalid Parentheses
- Score of Parentheses
