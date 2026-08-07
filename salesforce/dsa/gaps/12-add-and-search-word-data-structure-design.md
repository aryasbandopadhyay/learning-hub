# 12. Add and Search Word - Data structure design

- **Difficulty:** Medium
- **Pattern:** Trie / DFS
- **Asked at:** Salesforce, Amazon, Facebook

## Problem
Design `WordDictionary` with `addWord` and `search`, where `.` in a search pattern matches any single letter.

## Examples
```text
Input: addWord("bad"), addWord("dad"), addWord("mad"), search("pad"), search("bad"), search(".ad")
Output: [null,null,null,false,true,true]
Explanation: Dot can match b, d, or m.
```

## Understanding & Intuition
A trie handles exact prefixes. Wildcards create DFS branches only at positions containing `.`.

## Approach 1 — Naive / Brute Force
**Idea:** Store every word and compare each candidate.
```python
class WordDictionary:
    def __init__(self): self.words = []
    def addWord(self, word: str) -> None: self.words.append(word)
    def search(self, word: str) -> bool:
        for cand in self.words:
            if len(cand) == len(word) and all(a == "." or a == b for a, b in zip(word, cand)):
                return True
        return False
```
- **Time:** O(n * m) search — **Space:** O(n * m)

## Approach 2 — Better
**Idea:** Group words by length before scanning.
```python
class WordDictionary:
    def __init__(self): self.by_len = {}
    def addWord(self, word: str) -> None: self.by_len.setdefault(len(word), []).append(word)
    def search(self, word: str) -> bool:
        return any(all(a == "." or a == b for a, b in zip(word, cand)) for cand in self.by_len.get(len(word), []))
```
- **Time:** O(c * m) search — **Space:** O(n * m)

## Approach 3 — Optimal
**Idea:** Use a trie and recursively branch only on wildcard positions.
```python
class TrieNode:
    def __init__(self):
        self.children = {}; self.is_word = False
class WordDictionary:
    def __init__(self): self.root = TrieNode()
    def addWord(self, word: str) -> None:
        node = self.root
        for ch in word:
            node = node.children.setdefault(ch, TrieNode())
        node.is_word = True
    def search(self, word: str) -> bool:
        def dfs(i: int, node: TrieNode) -> bool:
            if i == len(word): return node.is_word
            ch = word[i]
            if ch == ".": return any(dfs(i + 1, child) for child in node.children.values())
            return ch in node.children and dfs(i + 1, node.children[ch])
        return dfs(0, self.root)
```
- **Time:** O(26^d * m) worst-case search — **Space:** O(n * m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * m) search | O(n * m) |
| Better | O(c * m) search | O(n * m) |
| Optimal | O(26^d * m) worst-case search | O(n * m) |

## Edge Cases & Pitfalls
- `.` matches exactly one character.
- Check `is_word`, not only prefix existence.
- Many wildcards can branch heavily.

## Related
- Implement Trie
- Design Search Autocomplete System
