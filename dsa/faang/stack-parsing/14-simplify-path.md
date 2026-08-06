# 14. Simplify Path

- **Difficulty:** Medium
- **Pattern:** Stack Parsing
- **Asked at:** Meta, Amazon, Microsoft, Google

## Problem
Given an absolute Unix-style path `path`, return its simplified canonical path. A single dot means the current directory, two dots mean the parent directory, and multiple consecutive slashes are treated as one slash. The result must start with `/` and must not end with `/` unless it is the root.

## Examples
```text
Input: path = "/a/./b/../../c/"
Output: "/c"
Explanation: Move into a, ignore '.', move into b, go back twice, then enter c.
```

## Understanding & Intuition
The path can be processed as directory tokens separated by slashes. Normal names are pushed, `..` pops one name if possible, and `.` or empty tokens are ignored. Joining the stack with slashes produces the canonical path.

## Approach 1 — Naive / Brute Force
**Idea:** Split on slashes, repeatedly rewrite the list by removing empty, dot, and parent-directory effects.
```python
class Solution:
    def simplifyPath(self, path: str) -> str:
        parts = path.split('/')
        changed = True
        while changed:
            changed = False
            new_parts = []
            i = 0
            while i < len(parts):
                token = parts[i]
                if token == '' or token == '.':
                    changed = True
                elif token == '..':
                    if new_parts:
                        new_parts.pop()
                    changed = True
                else:
                    new_parts.append(token)
                i += 1
            parts = new_parts
        return '/' + '/'.join(parts)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Manually scan characters to form path components and update a stack for each completed component.
```python
class Solution:
    def simplifyPath(self, path: str) -> str:
        stack = []
        token = []
        for ch in path + '/':
            if ch == '/':
                part = "".join(token)
                if part == '..':
                    if stack:
                        stack.pop()
                elif part and part != '.':
                    stack.append(part)
                token = []
            else:
                token.append(ch)
        return '/' + '/'.join(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use `split('/')` and a stack in one pass, ignoring empty and current-directory components.
```python
class Solution:
    def simplifyPath(self, path: str) -> str:
        stack = []
        for part in path.split('/'):
            if part == '' or part == '.':
                continue
            if part == '..':
                if stack:
                    stack.pop()
            else:
                stack.append(part)
        return '/' + '/'.join(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Extra slashes do not create directory names.
- At root, `..` keeps the path at root.
- Names like `...` are normal directory names, not parent traversal.

## Related
- Decode String
- Stack
