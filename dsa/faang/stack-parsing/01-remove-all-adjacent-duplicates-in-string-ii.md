# 01. Remove All Adjacent Duplicates in String II

- **Difficulty:** Medium
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Amazon, Google, Meta

## Problem
Given a string `s` and an integer `k`, repeatedly delete any group of exactly `k` adjacent equal characters. After each deletion, the remaining pieces concatenate and may form new deletable groups. Return the final string after no more deletions are possible.

Constraints: `1 <= len(s) <= 10^5`, `2 <= k <= 10^4`, and `s` contains lowercase English letters.

## Examples
```text
Input: s = "deeedbbcccbdaa", k = 3
Output: "aa"
Explanation: Delete "eee", then "ccc", then "bbb", leaving "aa".
```

## Understanding & Intuition
The string behaves like a stream where the only important state for a run is its character and current length modulo `k`. A stack is natural because deleting a run exposes the previous run as the new neighbor. The optimal solution compresses adjacent characters as it parses.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan the whole string, remove the first run of length at least `k`, and restart until a full pass finds nothing.
```python
class Solution:
    def removeDuplicates(self, s: str, k: int) -> str:
        changed = True
        while changed:
            changed = False
            i = 0
            parts = []
            while i < len(s):
                j = i
                while j < len(s) and s[j] == s[i]:
                    j += 1
                count = j - i
                if not changed and count >= k:
                    keep = count % k
                    parts.append(s[i] * keep)
                    changed = True
                else:
                    parts.append(s[i:j])
                i = j
            s = "".join(parts)
        return s
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep a stack of `[character, count]`; increment the top count for equal characters and pop when it reaches `k`.
```python
class Solution:
    def removeDuplicates(self, s: str, k: int) -> str:
        stack = []
        for ch in s:
            if stack and stack[-1][0] == ch:
                stack[-1][1] += 1
                if stack[-1][1] == k:
                    stack.pop()
            else:
                stack.append([ch, 1])
        return "".join(ch * count for ch, count in stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store output characters and their run counts in parallel arrays so the stack is represented directly by the answer buffer.
```python
class Solution:
    def removeDuplicates(self, s: str, k: int) -> str:
        chars = []
        counts = []
        for ch in s:
            chars.append(ch)
            if len(chars) > 1 and chars[-2] == ch:
                counts.append(counts[-1] + 1)
            else:
                counts.append(1)
            if counts[-1] == k:
                del chars[-k:]
                del counts[-k:]
        return "".join(chars)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A deletion can join two same-character runs that were previously separated.
- Counts should reset after popping a completed group.
- Runs longer than `k` may leave `count % k` characters.

## Related
- Remove Duplicate Letters
- Asteroid Collision
- Make The String Great
