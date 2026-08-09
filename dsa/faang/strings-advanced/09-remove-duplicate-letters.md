# 09. Remove Duplicate Letters

- **Difficulty:** Medium
- **Pattern:** advanced strings
- **Asked at:** Amazon, Google, Meta

## Problem
Given a string `s`, remove duplicate letters so every distinct letter appears exactly once. Return the lexicographically smallest valid subsequence.

**Input**
- `s`: a `str`; the input string.

**Output**
- A `str`. Return the lexicographically smallest valid subsequence.

## Constraints
- `1 <= len(s) <= 100000`.
- `s` contains lowercase English letters.

## Examples
```text
Input: s = "cbacdcbc"
Output: "acdb"
Explanation: "acdb" includes every distinct letter once and is the smallest valid subsequence.
```

## Understanding & Intuition
The answer must remain a subsequence. A chosen character can be removed only if it appears later. A monotonic stack keeps the current answer as small as possible while preserving all required characters.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly choose the smallest next character whose suffix still contains all remaining letters.
```python
class Solution:
    def removeDuplicateLetters(self, s: str) -> str:
        def solve(t):
            if not t:
                return ""
            need = set(t)
            for ch in sorted(need):
                pos = t.index(ch)
                if set(t[pos:]) == need:
                    return ch + solve(t[pos + 1:].replace(ch, ""))
            return ""
        return solve(s)
```
- **Time:** O(σ * n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Track remaining counts and pop larger stack letters only if they occur later.
```python
class Solution:
    def removeDuplicateLetters(self, s):
        from collections import Counter
        remaining = Counter(s)
        stack = []
        used = set()
        for ch in s:
            remaining[ch] -= 1
            if ch in used:
                continue
            while stack and ch < stack[-1] and remaining[stack[-1]] > 0:
                used.remove(stack.pop())
            stack.append(ch)
            used.add(ch)
        return ''.join(stack)
```
- **Time:** O(n) — **Space:** O(σ)

## Approach 3 — Optimal
**Idea:** Precompute last indices and maintain a monotonic stack of chosen characters.
```python
class Solution:
    def removeDuplicateLetters(self, s):
        last = {ch: i for i, ch in enumerate(s)}
        stack = []
        used = set()
        for i, ch in enumerate(s):
            if ch in used:
                continue
            while stack and ch < stack[-1] and last[stack[-1]] > i:
                used.remove(stack.pop())
            stack.append(ch)
            used.add(ch)
        return ''.join(stack)
```
- **Time:** O(n) — **Space:** O(σ)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(σ * n^2) | O(n) |
| Better | O(n) | O(σ) |
| Optimal | O(n) | O(σ) |

## Edge Cases & Pitfalls
- The result is a subsequence, not sorted characters.
- Pop only if the removed character appears later.
- Skip characters already present in the stack.

## Related
- Monotonic stack
- Greedy subsequence
