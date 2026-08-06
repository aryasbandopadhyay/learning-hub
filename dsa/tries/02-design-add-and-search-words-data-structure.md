# 02. Design Add and Search Words Data Structure

- **Difficulty:** Medium
- **Pattern:** Tries
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Design a data structure with `addWord(word)` and `search(word)`, where `search` may contain `.` matching any one lowercase English letter. Words contain lowercase letters and searches can be called many times.

## Examples
```text
Input: ["WordDictionary","addWord","addWord","addWord","search","search","search","search"]
       [[],["bad"],["dad"],["mad"],["pad"],["bad"],[".ad"],["b.."]]
Output: [null,null,null,null,false,true,true,true]
Explanation: "." branches to any character at that position.
```

## Understanding & Intuition
Exact word lookup is easy with a set, but `.` requires checking alternatives. A trie naturally branches only among existing characters, avoiding scans over irrelevant words. DFS is the standard way to evaluate wildcard positions.

## Approach 1 — Naive / Brute Force
**Idea:** Keep a list of words and compare every candidate character by character.
```python
class WordDictionary:
    def __init__(self):
        self.words = []

    def addWord(self, word: str) -> None:
        self.words.append(word)

    def search(self, word: str) -> bool:
        for candidate in self.words:
            if len(candidate) != len(word):
                continue
            # Every literal must match; '.' matches any one char.
            if all(p == "." or p == c for p, c in zip(word, candidate)):
                return True
        return False
```
- **Time:** O(NL) search, O(1) add — **Space:** O(total characters)

## Approach 2 — Better
**Idea:** Bucket words by length so wildcard searches only scan same-length words.
```python
from collections import defaultdict


class WordDictionary:
    def __init__(self):
        self.by_len = defaultdict(list)

    def addWord(self, word: str) -> None:
        self.by_len[len(word)].append(word)

    def search(self, word: str) -> bool:
        for candidate in self.by_len[len(word)]:
            ok = True
            for p, c in zip(word, candidate):
                if p != "." and p != c:
                    ok = False
                    break
            if ok:
                return True
        return False
```
- **Time:** O(KL) search for K words of that length, O(1) add — **Space:** O(total characters)

## Approach 3 — Optimal
**Idea:** Store words in a trie and DFS only when a wildcard is encountered.
```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_word = False


class WordDictionary:
    def __init__(self):
        self.root = TrieNode()

    def addWord(self, word: str) -> None:
        node = self.root
        for ch in word:
            if ch not in node.children:
                node.children[ch] = TrieNode()
            node = node.children[ch]
        node.is_word = True

    def search(self, word: str) -> bool:
        def dfs(i: int, node: TrieNode) -> bool:
            if i == len(word):
                return node.is_word
            ch = word[i]
            if ch == ".":
                # Try only real trie branches, not all 26 letters blindly.
                return any(dfs(i + 1, child) for child in node.children.values())
            return ch in node.children and dfs(i + 1, node.children[ch])

        return dfs(0, self.root)
```
- **Time:** O(26^D · L) worst-case with D wildcards, usually much less — **Space:** O(total trie nodes + recursion depth)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(NL) search | O(total characters) |
| Better | O(KL) search | O(total characters) |
| Optimal | O(26^D · L) worst-case search, O(L) add | O(total trie nodes) |

## Edge Cases & Pitfalls
- `.` matches exactly one character, not zero or many.
- Return true only at a terminal node after consuming the whole pattern.
- Avoid scanning words of different lengths.

## Related
- Implement Trie
- Regular expression matching
