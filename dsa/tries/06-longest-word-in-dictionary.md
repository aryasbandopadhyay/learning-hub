# 06. Longest Word in Dictionary

- **Difficulty:** Easy
- **Pattern:** Tries
- **Asked at:** Amazon, Google, Microsoft, Bloomberg

## Problem
Given a list of lowercase words, return the longest word that can be built one character at a time by other words in the list. If there is a tie, return the lexicographically smallest answer.

## Examples
```text
Input: words = ["w","wo","wor","worl","world"]
Output: "world"
Explanation: Every prefix of "world" is present as a word.
```

## Understanding & Intuition
A valid answer requires every prefix to also be a complete word. A set can check prefixes directly, while a trie can traverse only through terminal nodes. Sorting lexicographically makes tie-breaking straightforward.

## Approach 1 — Naive / Brute Force
**Idea:** For every word, check all prefixes in a set and update the best answer.
```python
from typing import List


class Solution:
    def longestWord(self, words: List[str]) -> str:
        seen = set(words)
        best = ""
        for word in words:
            valid = True
            for i in range(1, len(word) + 1):
                if word[:i] not in seen:
                    valid = False
                    break
            if valid and (len(word) > len(best) or (len(word) == len(best) and word < best)):
                best = word
        return best
```
- **Time:** O(NL²) due to slicing — **Space:** O(NL)

## Approach 2 — Better
**Idea:** Sort words, then build only words whose parent prefix is already buildable.
```python
from typing import List


class Solution:
    def longestWord(self, words: List[str]) -> str:
        buildable = {""}
        best = ""
        for word in sorted(words):
            # A word is buildable if removing its last char is buildable.
            if word[:-1] in buildable:
                buildable.add(word)
                if len(word) > len(best):
                    best = word
        return best
```
- **Time:** O(N log N · L) — **Space:** O(NL)

## Approach 3 — Optimal
**Idea:** Insert words into a trie, then DFS only through nodes that represent complete words.
```python
from typing import List


class TrieNode:
    def __init__(self):
        self.children = {}
        self.word = ""


class Solution:
    def longestWord(self, words: List[str]) -> str:
        root = TrieNode()
        for word in words:
            node = root
            for ch in word:
                if ch not in node.children:
                    node.children[ch] = TrieNode()
                node = node.children[ch]
            node.word = word

        best = ""

        def dfs(node: TrieNode) -> None:
            nonlocal best
            if node is not root:
                if not node.word:
                    return
                if len(node.word) > len(best) or (len(node.word) == len(best) and node.word < best):
                    best = node.word
            # Visit in sorted order so lexicographic ties are naturally handled.
            for ch in sorted(node.children):
                dfs(node.children[ch])

        dfs(root)
        return best
```
- **Time:** O(NL + E log E) for sorted child traversal — **Space:** O(NL)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(NL²) | O(NL) |
| Better | O(N log N · L) | O(NL) |
| Optimal | O(NL + E log E) | O(NL) |

## Edge Cases & Pitfalls
- Tie-break with lexicographically smallest word.
- A long word is invalid if even one prefix is missing.
- In trie DFS, do not continue through non-terminal nodes.

## Related
- Implement Trie
- Prefix validation problems
