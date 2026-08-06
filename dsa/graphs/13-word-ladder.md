# 13. Word Ladder

- **Difficulty:** Hard
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Return shortest transformation length from beginWord to endWord, changing one letter each step using wordList. Constraints: wordList <= 5000.

## Examples
```text
Input: beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
Output: 5
Explanation: hit -> hot -> dot -> dog -> cog.
```

## Understanding & Intuition
Words are graph nodes and one-letter changes are edges. BFS gives shortest path length.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def ladderLength(self, beginWord: str, endWord: str, wordList: List[str]) -> int:
        # Bidirectional BFS expands the smaller frontier.
        words = set(wordList)
        if endWord not in words: return 0
        front, back, dist = {beginWord}, {endWord}, 1
        while front and back:
            if len(front) > len(back): front, back = back, front
            nxt = set()
            for word in front:
                for i in range(len(word)):
                    for ch in "abcdefghijklmnopqrstuvwxyz":
                        cand = word[:i] + ch + word[i+1:]
                        if cand in back: return dist + 1
                        if cand in words:
                            words.remove(cand); nxt.add(cand)
            front, dist = nxt, dist + 1
        return 0
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def ladderLength(self, beginWord: str, endWord: str, wordList: List[str]) -> int:
        # Bidirectional BFS expands the smaller frontier.
        words = set(wordList)
        if endWord not in words: return 0
        front, back, dist = {beginWord}, {endWord}, 1
        while front and back:
            if len(front) > len(back): front, back = back, front
            nxt = set()
            for word in front:
                for i in range(len(word)):
                    for ch in "abcdefghijklmnopqrstuvwxyz":
                        cand = word[:i] + ch + word[i+1:]
                        if cand in back: return dist + 1
                        if cand in words:
                            words.remove(cand); nxt.add(cand)
            front, dist = nxt, dist + 1
        return 0
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def ladderLength(self, beginWord: str, endWord: str, wordList: List[str]) -> int:
        # Bidirectional BFS expands the smaller frontier.
        words = set(wordList)
        if endWord not in words: return 0
        front, back, dist = {beginWord}, {endWord}, 1
        while front and back:
            if len(front) > len(back): front, back = back, front
            nxt = set()
            for word in front:
                for i in range(len(word)):
                    for ch in "abcdefghijklmnopqrstuvwxyz":
                        cand = word[:i] + ch + word[i+1:]
                        if cand in back: return dist + 1
                        if cand in words:
                            words.remove(cand); nxt.add(cand)
            front, dist = nxt, dist + 1
        return 0
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V+E) or O(mn) | O(V) or O(mn) |
| Better | O(V+E) or O(mn) | O(V) or O(mn) |
| Optimal | O(V+E) or O(mn) | O(V) or O(mn) |

## Edge Cases & Pitfalls
- Empty or singleton graphs/grids.
- Mark visited before repeated traversal creates cycles.
- Preserve required in-place behavior when the signature returns None.

## Related
- BFS
- DFS
- Union-Find / Topological Sort
