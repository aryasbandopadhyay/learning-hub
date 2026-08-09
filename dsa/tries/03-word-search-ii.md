# 03. Word Search II

- **Difficulty:** Hard
- **Pattern:** Tries
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given an `m x n` board of lowercase letters and a list `words`, return every word that can be formed by walking horizontally or vertically adjacent cells. A cell may not be reused within one word, and each found word is returned at most once.

**Input**
- `board`: a 2-D grid of lowercase characters.
- `words`: target words to search for.

**Output**
- A list of all target words present on the board. The judge accepts any order for this problem.

## Constraints
- `m == board.length`
- `n == board[r].length`
- `1 <= m, n <= 12`
- `1 <= words.length <= 3 * 10^4`
- `1 <= words[i].length <= 10`
- Board cells and words contain lowercase English letters.
- All words are unique.

## Examples
```text
Input: board = [["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]],
       words = ["oath","pea","eat","rain"]
Output: ["eat","oath"]
Explanation: `oath` and `eat` can be traced through adjacent cells without reusing a cell; `pea` and `rain` cannot.
```

## Understanding & Intuition
Searching the board separately for every word repeats the same prefix work. A trie lets the DFS abandon a path as soon as its current prefix is not in any word. Storing the completed word at terminal nodes also avoids rebuilding strings repeatedly.

## Approach 1 — Naive / Brute Force
**Idea:** For every word, run a board DFS that tries to spell only that word.
```python
from typing import List


class Solution:
    def findWords(self, board: List[List[str]], words: List[str]) -> List[str]:
        rows, cols = len(board), len(board[0])

        def exists(word: str) -> bool:
            def dfs(r: int, c: int, i: int) -> bool:
                if i == len(word):
                    return True
                if r < 0 or r == rows or c < 0 or c == cols or board[r][c] != word[i]:
                    return False
                ch = board[r][c]
                board[r][c] = "#"  # Mark visited for this path.
                found = (dfs(r + 1, c, i + 1) or dfs(r - 1, c, i + 1) or
                         dfs(r, c + 1, i + 1) or dfs(r, c - 1, i + 1))
                board[r][c] = ch
                return found

            return any(dfs(r, c, 0) for r in range(rows) for c in range(cols))

        return [word for word in words if exists(word)]
```
- **Time:** O(W · MN · 4^L) — **Space:** O(L) recursion

## Approach 2 — Better
**Idea:** Build a dictionary-based trie, DFS once from each cell, and collect terminal words.
```python
from typing import List


class Solution:
    def findWords(self, board: List[List[str]], words: List[str]) -> List[str]:
        root = {}
        for word in words:
            node = root
            for ch in word:
                node = node.setdefault(ch, {})
            node["$"] = word

        rows, cols = len(board), len(board[0])
        ans = []

        def dfs(r: int, c: int, parent: dict) -> None:
            ch = board[r][c]
            if ch not in parent:
                return
            node = parent[ch]
            word = node.pop("$", None)
            if word is not None:
                ans.append(word)

            board[r][c] = "#"
            for nr, nc in ((r + 1, c), (r - 1, c), (r, c + 1), (r, c - 1)):
                if 0 <= nr < rows and 0 <= nc < cols and board[nr][nc] != "#":
                    dfs(nr, nc, node)
            board[r][c] = ch

        for r in range(rows):
            for c in range(cols):
                dfs(r, c, root)
        return ans
```
- **Time:** O(MN · 4^L + total word length) worst-case — **Space:** O(total trie nodes + L)

## Approach 3 — Optimal
**Idea:** Use `TrieNode` objects with terminal words and prune dead leaves after DFS.
```python
from typing import List


class TrieNode:
    def __init__(self):
        self.children = {}
        self.word = None


class Solution:
    def findWords(self, board: List[List[str]], words: List[str]) -> List[str]:
        root = TrieNode()
        for word in words:
            node = root
            for ch in word:
                if ch not in node.children:
                    node.children[ch] = TrieNode()
                node = node.children[ch]
            node.word = word

        rows, cols = len(board), len(board[0])
        ans = []

        def dfs(r: int, c: int, parent: TrieNode) -> None:
            ch = board[r][c]
            node = parent.children.get(ch)
            if node is None:
                return
            if node.word is not None:
                ans.append(node.word)
                node.word = None  # Prevent duplicate output.

            board[r][c] = "#"
            for nr, nc in ((r + 1, c), (r - 1, c), (r, c + 1), (r, c - 1)):
                if 0 <= nr < rows and 0 <= nc < cols and board[nr][nc] != "#":
                    dfs(nr, nc, node)
            board[r][c] = ch

            if not node.children and node.word is None:
                del parent.children[ch]  # Future paths can skip this branch.

        for r in range(rows):
            for c in range(cols):
                dfs(r, c, root)
        return ans
```
- **Time:** O(MN · 4^L + total word length), pruned in practice — **Space:** O(total trie nodes + L)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(W · MN · 4^L) | O(L) |
| Better | O(MN · 4^L + total word length) | O(total trie nodes + L) |
| Optimal | O(MN · 4^L + total word length), pruned | O(total trie nodes + L) |

## Edge Cases & Pitfalls
- Do not reuse a board cell within one word path.
- Remove or clear terminal words after finding them to avoid duplicates.
- Mutating the board requires restoring the character before returning.

## Related
- Word Search
- Boggle solver
