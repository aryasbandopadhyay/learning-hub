# 07. Make The String Great

- **Difficulty:** Medium
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Amazon, Google, Microsoft

## Problem
A string is good if it contains no adjacent pair of the same letter in opposite cases, such as `"aA"` or `"Bb"`. Given a string `s`, repeatedly remove any bad adjacent pair until the string is good, and return the final string.

Constraints: `1 <= len(s) <= 10^5`; `s` contains English letters.

## Examples
```text
Input: s = "leEeetcode"
Output: "leetcode"
Explanation: Removing "eE" leaves a good string.
```

## Understanding & Intuition
Removing a bad pair can make the characters around it adjacent, which may create a new bad pair. This last-in, first-out behavior is exactly what a stack captures. Two letters conflict when their lowercase forms match but their cases differ.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan for the first bad adjacent pair, delete it, and restart.
```python
class Solution:
    def makeGood(self, s: str) -> str:
        def bad(a, b):
            return a != b and a.lower() == b.lower()
        changed = True
        while changed:
            changed = False
            i = 0
            out = []
            while i < len(s):
                if i + 1 < len(s) and bad(s[i], s[i + 1]) and not changed:
                    i += 2
                    changed = True
                else:
                    out.append(s[i])
                    i += 1
            s = ''.join(out)
        return s
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack; if the new character conflicts with the top, pop the top instead of pushing.
```python
class Solution:
    def makeGood(self, s: str) -> str:
        stack = []
        for ch in s:
            if stack and stack[-1] != ch and stack[-1].lower() == ch.lower():
                stack.pop()
            else:
                stack.append(ch)
        return ''.join(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Compare ASCII codes: opposite cases of the same letter differ by 32, avoiding repeated lowercase conversions.
```python
class Solution:
    def makeGood(self, s: str) -> str:
        stack = []
        for ch in s:
            if stack and abs(ord(stack[-1]) - ord(ch)) == 32:
                stack.pop()
            else:
                stack.append(ch)
        return ''.join(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Chain reactions can remove characters far apart in the original string.
- Same case letters such as `"aa"` are allowed.
- The empty string is a valid final answer.

## Related
- Remove All Adjacent Duplicates in String II
- Backspace String Compare
- Valid Parentheses
