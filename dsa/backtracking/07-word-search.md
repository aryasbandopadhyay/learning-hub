# 07. Word Search

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an `m x n` grid of characters `board` and a string `word`, return `True` if `word` exists in the grid. Adjacent letters are horizontally or vertically neighboring, and the same cell may not be used more than once. `1 <= m,n <= 6`, `1 <= len(word) <= 15`.

## Examples
```text
Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
Output: true
Explanation: The path A -> B -> C -> C -> E -> D forms the word.
```

## Understanding & Intuition
The word is a path through the grid. From each matching starting cell, backtracking tries four directions while marking cells as visited. Pruning by letter counts can reject impossible words before the search.

## Approach 1 — Naive / Brute Force
**Idea:** Start a DFS from every cell and use a visited set to prevent reusing cells.
```python
from typing import List

class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        rows, cols = len(board), len(board[0])

        def dfs(r: int, c: int, i: int, seen: set) -> bool:
            if i == len(word):
                return True
            if r < 0 or r == rows or c < 0 or c == cols:
                return False
            if (r, c) in seen or board[r][c] != word[i]:
                return False
            seen.add((r, c))
            ok = (dfs(r + 1, c, i + 1, seen) or
                  dfs(r - 1, c, i + 1, seen) or
                  dfs(r, c + 1, i + 1, seen) or
                  dfs(r, c - 1, i + 1, seen))
            seen.remove((r, c))
            return ok

        for r in range(rows):
            for c in range(cols):
                if dfs(r, c, 0, set()):
                    return True
        return False
```
- **Time:** O(mn * 4^L) — **Space:** O(L)

## Approach 2 — Better
**Idea:** Mark visited cells in-place to avoid set overhead.
```python
from typing import List

class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        rows, cols = len(board), len(board[0])

        def dfs(r: int, c: int, i: int) -> bool:
            if i == len(word):
                return True
            if r < 0 or r == rows or c < 0 or c == cols or board[r][c] != word[i]:
                return False
            saved = board[r][c]
            board[r][c] = "#"
            ok = (dfs(r + 1, c, i + 1) or
                  dfs(r - 1, c, i + 1) or
                  dfs(r, c + 1, i + 1) or
                  dfs(r, c - 1, i + 1))
            board[r][c] = saved
            return ok

        return any(dfs(r, c, 0) for r in range(rows) for c in range(cols))
```
- **Time:** O(mn * 3^L) — **Space:** O(L)

## Approach 3 — Optimal
**Idea:** Reject impossible letter counts, optionally reverse the word to start from the rarer end, then use in-place backtracking.
```python
from collections import Counter
from typing import List

class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        rows, cols = len(board), len(board[0])
        board_count = Counter(ch for row in board for ch in row)
        word_count = Counter(word)
        if any(board_count[ch] < need for ch, need in word_count.items()):
            return False
        if board_count[word[-1]] < board_count[word[0]]:
            word = word[::-1]

        def dfs(r: int, c: int, i: int) -> bool:
            if i == len(word):
                return True
            if r < 0 or r == rows or c < 0 or c == cols or board[r][c] != word[i]:
                return False
            saved = board[r][c]
            board[r][c] = "#"
            ok = (dfs(r + 1, c, i + 1) or
                  dfs(r - 1, c, i + 1) or
                  dfs(r, c + 1, i + 1) or
                  dfs(r, c - 1, i + 1))
            board[r][c] = saved
            return ok

        return any(dfs(r, c, 0) for r in range(rows) for c in range(cols))
```
- **Time:** O(mn * 3^L) worst case — **Space:** O(L + A), where `A` is alphabet size

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn * 4^L) | O(L) |
| Better | O(mn * 3^L) | O(L) |
| Optimal | O(mn * 3^L) | O(L + A) |

## Edge Cases & Pitfalls
- Restore the board cell before returning.
- Do not reuse the same cell in a path.
- A word can start at any cell, not only the top-left.

## Related
- N-Queens
- Sudoku Solver
- Palindrome Partitioning
