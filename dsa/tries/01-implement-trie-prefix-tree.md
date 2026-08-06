# 01. Implement Trie (Prefix Tree)

- **Difficulty:** Medium
- **Pattern:** Tries
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Design a trie supporting `insert(word)`, `search(word)`, and `startsWith(prefix)` for lowercase English strings. Each operation should handle words/prefixes of length up to about `2000`, with many operations over the same data structure.

## Examples
```text
Input: ["Trie","insert","search","search","startsWith","insert","search"]
       [[],["apple"],["apple"],["app"],["app"],["app"],["app"]]
Output: [null,null,true,false,true,null,true]
Explanation: Insert "apple"; "app" is only a prefix until it is inserted.
```

## Understanding & Intuition
A trie stores shared prefixes once, so repeated prefix queries do not rescan all inserted words. A word ends at a special terminal marker, because a prefix is not always a complete word. The design tradeoff is between simple sets and an explicit trie structure.

## Approach 1 — Naive / Brute Force
**Idea:** Store all words and all prefixes in hash sets while inserting.
```python
class Trie:
    def __init__(self):
        self.words = set()
        self.prefixes = set()

    def insert(self, word: str) -> None:
        self.words.add(word)
        # Precompute every prefix for fast startsWith.
        for i in range(1, len(word) + 1):
            self.prefixes.add(word[:i])

    def search(self, word: str) -> bool:
        return word in self.words

    def startsWith(self, prefix: str) -> bool:
        return prefix in self.prefixes
```
- **Time:** O(L²) insert due to slicing, O(L) search/hash — **Space:** O(total prefix characters)

## Approach 2 — Better
**Idea:** Use nested dictionaries and a terminal key to represent the trie.
```python
class Trie:
    def __init__(self):
        self.root = {}

    def insert(self, word: str) -> None:
        node = self.root
        for ch in word:
            # Create the next dictionary only when needed.
            node = node.setdefault(ch, {})
        node["#"] = True

    def search(self, word: str) -> bool:
        node = self.root
        for ch in word:
            if ch not in node:
                return False
            node = node[ch]
        return "#" in node

    def startsWith(self, prefix: str) -> bool:
        node = self.root
        for ch in prefix:
            if ch not in node:
                return False
            node = node[ch]
        return True
```
- **Time:** O(L) per operation — **Space:** O(total trie nodes)

## Approach 3 — Optimal
**Idea:** Use a small `TrieNode` class with child links and an `is_word` flag.
```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_word = False


class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word: str) -> None:
        node = self.root
        for ch in word:
            if ch not in node.children:
                node.children[ch] = TrieNode()
            node = node.children[ch]
        node.is_word = True

    def _walk(self, text: str):
        node = self.root
        for ch in text:
            if ch not in node.children:
                return None
            node = node.children[ch]
        return node

    def search(self, word: str) -> bool:
        node = self._walk(word)
        return node is not None and node.is_word

    def startsWith(self, prefix: str) -> bool:
        return self._walk(prefix) is not None
```
- **Time:** O(L) per operation — **Space:** O(total trie nodes)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(L²) insert, O(L) query | O(total prefix characters) |
| Better | O(L) per operation | O(total trie nodes) |
| Optimal | O(L) per operation | O(total trie nodes) |

## Edge Cases & Pitfalls
- A prefix like `"app"` is not a word until explicitly inserted.
- Mark word endings separately from child existence.
- Empty strings are uncommon in LeetCode input, but the terminal marker/flag handles them.

## Related
- Word Dictionary with wildcards
- Map Sum Pairs
