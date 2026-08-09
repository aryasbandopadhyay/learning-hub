# 04. Generate Parentheses

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer `n`, generate every well-formed parentheses string containing exactly `n` opening and `n` closing parentheses.

**Input**
- `n`: the number of pairs of parentheses.

**Output**
- A list of valid strings. **This judge compares exactly**: return strings in the standard backtracking order that tries `(` before `)`, lexicographic with `(` before `)`.

## Constraints
- `1 <= n <= 8`

## Examples
```text
Input: n = 3
Output: ["((()))","(()())","(())()","()(())","()()()"]
Explanation: For `n = 3`, the five shown strings are the only ways to place three pairs while every prefix has at least as many `(` as `)`.
```

## Understanding & Intuition
A valid prefix never has more closing than opening parentheses. Backtracking builds only prefixes that can still become valid. The call stack or an explicit stack records the current partial string.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every length `2n` string of parentheses and keep only valid ones.
```python
class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        ans = []

        def valid(s: str) -> bool:
            balance = 0
            for ch in s:
                balance += 1 if ch == "(" else -1
                if balance < 0:
                    return False
            return balance == 0

        def build(s: str) -> None:
            if len(s) == 2 * n:
                if valid(s):
                    ans.append(s)
                return
            build(s + "(")
            build(s + ")")

        build("")
        return ans
```
- **Time:** O(2^(2n) * n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Backtrack with counts, pruning invalid prefixes immediately.
```python
class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        ans = []

        def dfs(s: str, opened: int, closed: int) -> None:
            if len(s) == 2 * n:
                ans.append(s)
                return
            # Add '(' if we still have an unused opening bracket.
            if opened < n:
                dfs(s + "(", opened + 1, closed)
            # Add ')' only when it will not make the prefix invalid.
            if closed < opened:
                dfs(s + ")", opened, closed + 1)

        dfs("", 0, 0)
        return ans
```
- **Time:** O(C_n * n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use one mutable path list to avoid creating many intermediate strings during backtracking.
```python
class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        ans = []
        path = []

        def dfs(opened: int, closed: int) -> None:
            if len(path) == 2 * n:
                ans.append("".join(path))
                return
            if opened < n:
                path.append("(")
                dfs(opened + 1, closed)
                path.pop()
            if closed < opened:
                path.append(")")
                dfs(opened, closed + 1)
                path.pop()

        dfs(0, 0)
        return ans
```
- **Time:** O(C_n * n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(2n) * n) | O(n) |
| Better | O(C_n * n) | O(n) |
| Optimal | O(C_n * n) | O(n) |

## Edge Cases & Pitfalls
- Never place `)` when `closed == opened`.
- The output order commonly follows choosing `(` before `)`.
- `C_n` is the nth Catalan number, the count of valid strings.

## Related
- Valid Parentheses
- Decode String

