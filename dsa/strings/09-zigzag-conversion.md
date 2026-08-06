# 09. Zigzag Conversion

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Amazon, PayPal, Microsoft, Google

## Problem
Write the characters of `s` in a zigzag pattern on `numRows` rows, then read row by row. Return the resulting string. Constraints: `1 <= len(s) <= 1000`; `1 <= numRows <= 1000`.

## Examples
```text
Input: s = "PAYPALISHIRING", numRows = 3
Output: "PAHNAPLSIIGYIR"
Explanation: Reading the 3-row zigzag row by row gives the output.
```

## Understanding & Intuition
Characters move down rows, then diagonally up, repeating in cycles. We can simulate this movement or compute which indices belong to each row. The cycle length is `2 * numRows - 2` when there is more than one row.

## Approach 1 — Naive / Brute Force
**Idea:** Fill a sparse grid following the zigzag path, then read non-empty cells row-wise.
```python
class Solution:
    def convert(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s
        grid = [[""] * len(s) for _ in range(numRows)]
        row, col, direction = 0, 0, 1
        for ch in s:
            grid[row][col] = ch
            if direction == 1:
                if row == numRows - 1:
                    direction = -1
                    row -= 1
                    col += 1
                else:
                    row += 1
            else:
                if row == 0:
                    direction = 1
                    row += 1
                else:
                    row -= 1
                    col += 1
        return ''.join(ch for line in grid for ch in line if ch)
```
- **Time:** O(n * r) — **Space:** O(n * r)

## Approach 2 — Better
**Idea:** Simulate only row buckets and flip direction at the top and bottom.
```python
class Solution:
    def convert(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s
        rows = [[] for _ in range(numRows)]
        row, step = 0, 1
        for ch in s:
            rows[row].append(ch)
            if row == 0:
                step = 1
            elif row == numRows - 1:
                step = -1
            row += step
        return ''.join(''.join(line) for line in rows)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Compute row indices directly using the zigzag cycle and append characters in reading order.
```python
class Solution:
    def convert(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s
        cycle = 2 * numRows - 2
        result = []
        for row in range(numRows):
            for i in range(row, len(s), cycle):
                result.append(s[i])
                diagonal = i + cycle - 2 * row
                if 0 < row < numRows - 1 and diagonal < len(s):
                    result.append(s[diagonal])
        return ''.join(result)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * r) | O(n * r) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- If `numRows` is `1`, no zigzag occurs.
- If `numRows >= len(s)`, the output is unchanged.
- Middle rows have both vertical and diagonal characters in each cycle.

## Related
- Matrix Simulation
- String Reconstruction
- Pattern Indexing
