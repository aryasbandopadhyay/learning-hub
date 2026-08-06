# 04. Word Search

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Meta, Apple

## Problem
Given an `m x n` board of characters and a word, return whether the word exists by moving horizontally or vertically to adjacent cells. A cell may not be reused in one path. Constraints: `1 <= m,n <= 6`, `1 <= len(word) <= 15`.

## Examples
```text
Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
Output: true
Explanation: A path A -> B -> C -> C -> E -> D exists.
```

## Understanding & Intuition
This is path search in a grid, not simple substring matching. From each matching first letter, try all four directions while preventing reuse. Pruning by character counts can reject impossible words early.

## Approach 1 — Naive / Brute Force
**Idea:** Start DFS from every cell without preliminary pruning.
```python
from typing import List

class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        m, n = len(board), len(board[0])

        def dfs(r: int, c: int, i: int, used: set[tuple[int, int]]) -> bool:
            if i == len(word):
                return True
            if not (0 <= r < m and 0 <= c < n):
                return False
            if (r, c) in used or board[r][c] != word[i]:
                return False

            used.add((r, c))
            found = (
                dfs(r + 1, c, i + 1, used) or
                dfs(r - 1, c, i + 1, used) or
                dfs(r, c + 1, i + 1, used) or
                dfs(r, c - 1, i + 1, used)
            )
            used.remove((r, c))
            return found

        return any(dfs(r, c, 0, set()) for r in range(m) for c in range(n))
```
- **Time:** O(mn * 4^L) — **Space:** O(L)

## Approach 2 — Better
**Idea:** Count board letters first and only DFS if the board has enough of each required character.
```python
from collections import Counter
from typing import List

class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        counts = Counter(ch for row in board for ch in row)
        need = Counter(word)
        if any(counts[ch] < need[ch] for ch in need):
            return False

        m, n = len(board), len(board[0])
        seen = [[False] * n for _ in range(m)]

        def dfs(r: int, c: int, i: int) -> bool:
            if i == len(word):
                return True
            if not (0 <= r < m and 0 <= c < n):
                return False
            if seen[r][c] or board[r][c] != word[i]:
                return False
            seen[r][c] = True
            ok = any(dfs(r + dr, c + dc, i + 1) for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)))
            seen[r][c] = False
            return ok

        return any(dfs(r, c, 0) for r in range(m) for c in range(n))
```
- **Time:** O(mn * 4^L) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Add frequency pruning, start from the rarer end of the word, and mark visited cells in-place.
```python
from collections import Counter
from typing import List

class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        m, n = len(board), len(board[0])
        counts = Counter(ch for row in board for ch in row)
        need = Counter(word)
        if any(counts[ch] < need[ch] for ch in need):
            return False
        if counts[word[-1]] < counts[word[0]]:
            word = word[::-1]

        def dfs(r: int, c: int, i: int) -> bool:
            if i == len(word):
                return True
            if not (0 <= r < m and 0 <= c < n) or board[r][c] != word[i]:
                return False

            saved = board[r][c]
            board[r][c] = "#"  # Temporary sentinel prevents reuse.
            ok = (
                dfs(r + 1, c, i + 1) or
                dfs(r - 1, c, i + 1) or
                dfs(r, c + 1, i + 1) or
                dfs(r, c - 1, i + 1)
            )
            board[r][c] = saved
            return ok

        return any(board[r][c] == word[0] and dfs(r, c, 0) for r in range(m) for c in range(n))
```
- **Time:** O(mn * 3^L) — **Space:** O(L)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn * 4^L) | O(L) |
| Better | O(mn * 4^L) | O(mn) |
| Optimal | O(mn * 3^L) | O(L) |

## Edge Cases & Pitfalls
- A cell cannot be reused in the same path.
- Restore in-place markers before returning from DFS.

## Related
- Number of Islands
- Word Search II
