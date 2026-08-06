# 16. Zigzag Conversion

- **Difficulty:** Medium
- **Pattern:** String Simulation
- **Asked at:** Amazon, Meta, Microsoft

## Problem
Given a string `s` and an integer `numRows`, write the characters in a zigzag pattern across `numRows` rows and then read row by row. Return the converted string. Constraints include `1 <= numRows <= 1000` and `1 <= len(s) <= 1000`.

## Examples
```text
Input: s = "PAYPALISHIRING", numRows = 3
Output: "PAHNAPLSIIGYIR"
Explanation: Reading the three-row zigzag line by line gives the output.
```

## Understanding & Intuition
The zigzag path moves down row by row, then diagonally up until it reaches the top. A cycle has length `2 * numRows - 2` when more than one row exists. We can either simulate the path or compute the indices row by row.

## Approach 1 — Naive / Brute Force
**Idea:** Place characters into a sparse grid following down and diagonal moves, then read the filled cells row by row.
```python
class Solution:
    def convert(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s
        grid = [[""] * len(s) for _ in range(numRows)]
        row = col = 0
        going_down = True
        for ch in s:
            grid[row][col] = ch
            if going_down:
                if row == numRows - 1:
                    going_down = False
                    row -= 1
                    col += 1
                else:
                    row += 1
            else:
                if row == 0:
                    going_down = True
                    row += 1
                else:
                    row -= 1
                    col += 1
        out = []
        for r in range(numRows):
            for c in range(len(s)):
                if grid[r][c]:
                    out.append(grid[r][c])
        return "".join(out)
```
- **Time:** O(n * numRows) — **Space:** O(n * numRows)

## Approach 2 — Better
**Idea:** Simulate only the current row and append each character to that row's string builder.
```python
class Solution:
    def convert(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s
        rows = [[] for _ in range(numRows)]
        row = 0
        step = 1
        for ch in s:
            rows[row].append(ch)
            if row == 0:
                step = 1
            elif row == numRows - 1:
                step = -1
            row += step
        return "".join("".join(r) for r in rows)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Read characters by row using the zigzag cycle length, adding the vertical and diagonal positions directly.
```python
class Solution:
    def convert(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s
        cycle = 2 * numRows - 2
        ans = []
        for row in range(numRows):
            i = row
            while i < len(s):
                ans.append(s[i])
                diag = i + cycle - 2 * row
                if 0 < row < numRows - 1 and diag < len(s):
                    ans.append(s[diag])
                i += cycle
        return "".join(ans)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * numRows) | O(n * numRows) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- If `numRows` is `1`, the string is unchanged.
- If there are at least as many rows as characters, the string is unchanged.
- Middle rows contribute both vertical and diagonal characters in each cycle.

## Related
- String Simulation
- Matrix Traversal
