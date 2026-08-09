# 12. Simplify Path

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an absolute Unix-style file path, simplify it to canonical form. `.` means current directory, `..` moves to the parent if possible, and repeated slashes act as one separator.

**Input**
- `path`: an absolute path string starting with `/`.

**Output**
- The canonical absolute path. **This judge compares exactly**: start with one `/`, use single separators, have no trailing slash unless root, and contain no `.` or `..` components.

## Constraints
- `1 <= path.length <= 3000`
- `path` consists of English letters, digits, `.`, `_`, and `/`.
- `path` is an absolute Unix path.

## Examples
```text
Input: path = "/home//foo/"
Output: "/home/foo"
Explanation: The repeated slash between `home` and `foo` collapses to one separator, and the trailing slash is removed.
```

## Understanding & Intuition
Path components are processed left to right. Normal names are pushed, while `..` pops the most recent directory when possible. This is a direct stack model of entering and leaving directories.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly normalize obvious string patterns, then process parent references.
```python
class Solution:
    def simplifyPath(self, path: str) -> str:
        parts = [p for p in path.split("/") if p != "" and p != "."]
        changed = True
        while changed:
            changed = False
            for i, part in enumerate(parts):
                if part == "..":
                    start = max(0, i - 1)
                    parts[start:i + 1] = []
                    changed = True
                    break
        return "/" + "/".join(p for p in parts if p != "..")
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Split into components and use a stack for directory names.
```python
class Solution:
    def simplifyPath(self, path: str) -> str:
        stack = []
        for part in path.split("/"):
            if part == "" or part == ".":
                continue
            if part == "..":
                if stack:
                    stack.pop()
            else:
                stack.append(part)
        return "/" + "/".join(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Same stack logic, using a set of ignored components for concise canonical processing.
```python
class Solution:
    def simplifyPath(self, path: str) -> str:
        stack = []
        skip = {"", "."}
        for part in path.split("/"):
            if part in skip:
                continue
            if part == "..":
                if stack:
                    stack.pop()
            else:
                # Names like "..." are valid directory names.
                stack.append(part)
        return "/" + "/".join(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Going above root keeps the path at root.
- `"..."` is a normal directory name, not parent traversal.
- The result must start with `/` and not end with `/` unless it is root.

## Related
- Decode String
- Basic Calculator

