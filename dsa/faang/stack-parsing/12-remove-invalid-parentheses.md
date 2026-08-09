# 12. Remove Invalid Parentheses

- **Difficulty:** Hard
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Meta, Google, Amazon

## Problem
Given a string `s` containing letters and parentheses, remove the minimum number of invalid parentheses so every returned string is valid. Return all possible results sorted lexicographically to make the output deterministic.

**Input**
- `s`: a `str`; the input string.

**Output**
- A `list[str]`. returned string is valid. Return all possible results sorted lexicographically to make the output deterministic. This judge compares the sequence exactly: return all valid strings that use the minimum removals, sorted lexicographically.

## Constraints
- `1 <= len(s) <= 25` and `s` contains lowercase letters, `'('`, and `')'`.

## Examples
```text
Input: s = "()())()"
Output: ["(())()", "()()()"]
Explanation: Removing one of the two extra closing parentheses gives two valid strings. The output is written in the required deterministic order.
```

## Understanding & Intuition
The minimum-removal requirement means we should stop as soon as any valid strings are found at a removal depth. A stack-style balance check validates candidates by rejecting prefixes with too many closing parentheses. DFS can be more direct by precomputing how many left and right parentheses must be removed.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subsequences, keep those that are valid, and choose only the longest valid strings.
```python
class Solution:
    def removeInvalidParentheses(self, s: str) -> list[str]:
        def valid(t):
            bal = 0
            for ch in t:
                if ch == '(':
                    bal += 1
                elif ch == ')':
                    bal -= 1
                    if bal < 0:
                        return False
            return bal == 0
        best_len = -1
        ans = set()
        n = len(s)
        for mask in range(1 << n):
            if mask.bit_count() < best_len:
                continue
            t = ''.join(s[i] for i in range(n) if mask & (1 << i))
            if valid(t):
                if len(t) > best_len:
                    best_len = len(t)
                    ans.clear()
                ans.add(t)
        return sorted(ans)
```
- **Time:** O(n * 2^n) — **Space:** O(2^n)

## Approach 2 — Better
**Idea:** BFS by deletion depth; the first level containing valid strings is exactly the minimum-removal level.
```python
class Solution:
    def removeInvalidParentheses(self, s: str) -> list[str]:
        def valid(t):
            bal = 0
            for ch in t:
                if ch == '(':
                    bal += 1
                elif ch == ')':
                    bal -= 1
                    if bal < 0:
                        return False
            return bal == 0
        level = {s}
        while True:
            good = [t for t in level if valid(t)]
            if good:
                return sorted(good)
            nxt = set()
            for t in level:
                for i, ch in enumerate(t):
                    if ch in '()':
                        nxt.add(t[:i] + t[i + 1:])
            level = nxt
```
- **Time:** O(n * 2^n) — **Space:** O(2^n)

## Approach 3 — Optimal
**Idea:** Count the exact number of left and right parentheses to remove, then DFS while maintaining balance and skipping duplicate removals.
```python
class Solution:
    def removeInvalidParentheses(self, s: str) -> list[str]:
        lrem = rrem = 0
        for ch in s:
            if ch == '(':
                lrem += 1
            elif ch == ')':
                if lrem:
                    lrem -= 1
                else:
                    rrem += 1
        ans = set()
        def dfs(i, bal, lrem, rrem, path):
            if i == len(s):
                if bal == 0 and lrem == 0 and rrem == 0:
                    ans.add(''.join(path))
                return
            ch = s[i]
            if ch == '(':
                if lrem:
                    dfs(i + 1, bal, lrem - 1, rrem, path)
                path.append(ch)
                dfs(i + 1, bal + 1, lrem, rrem, path)
                path.pop()
            elif ch == ')':
                if rrem:
                    dfs(i + 1, bal, lrem, rrem - 1, path)
                if bal:
                    path.append(ch)
                    dfs(i + 1, bal - 1, lrem, rrem, path)
                    path.pop()
            else:
                path.append(ch)
                dfs(i + 1, bal, lrem, rrem, path)
                path.pop()
        dfs(0, 0, lrem, rrem, [])
        return sorted(ans)
```
- **Time:** O(n * 2^n) — **Space:** O(2^n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * 2^n) | O(2^n) |
| Better | O(n * 2^n) | O(2^n) |
| Optimal | O(n * 2^n) | O(2^n) |

## Edge Cases & Pitfalls
- Return every minimum-removal result, not just one.
- Sort the final list for deterministic judging.
- Letters are always kept unless a subsequence enumeration naturally includes them; optimal DFS keeps all letters.

## Related
- Minimum Remove to Make Valid Parentheses
- Valid Parentheses
- Generate Parentheses
