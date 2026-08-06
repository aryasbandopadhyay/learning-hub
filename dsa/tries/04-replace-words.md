# 04. Replace Words

- **Difficulty:** Medium
- **Pattern:** Tries
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given a dictionary of root words and a sentence, replace every word in the sentence with the shortest root that is a prefix of that word. If no root matches, keep the word unchanged. Words are lowercase English strings separated by single spaces.

## Examples
```text
Input: dictionary = ["cat","bat","rat"], sentence = "the cattle was rattled by the battery"
Output: "the cat was rat by the bat"
Explanation: "cattle", "rattled", and "battery" use their shortest matching roots.
```

## Understanding & Intuition
For each sentence word, we only care about the earliest prefix that appears in the root dictionary. A set gives a simple prefix check, but a trie can stop scanning as soon as no root can continue. The terminal marker ensures the shortest root is returned immediately.

## Approach 1 — Naive / Brute Force
**Idea:** Put roots in a set and test every growing prefix of each sentence word.
```python
from typing import List


class Solution:
    def replaceWords(self, dictionary: List[str], sentence: str) -> str:
        roots = set(dictionary)
        result = []
        for word in sentence.split():
            replacement = word
            for i in range(1, len(word) + 1):
                # The first matching prefix is the shortest root.
                if word[:i] in roots:
                    replacement = word[:i]
                    break
            result.append(replacement)
        return " ".join(result)
```
- **Time:** O(S · L²) due to slicing — **Space:** O(R + S)

## Approach 2 — Better
**Idea:** Sort roots by length and find the first root that prefixes each word.
```python
from typing import List


class Solution:
    def replaceWords(self, dictionary: List[str], sentence: str) -> str:
        roots = sorted(dictionary, key=len)

        def replace(word: str) -> str:
            for root in roots:
                # Because roots are sorted, the first match is shortest.
                if word.startswith(root):
                    return root
            return word

        return " ".join(replace(word) for word in sentence.split())
```
- **Time:** O(S · R · L) — **Space:** O(R + S)

## Approach 3 — Optimal
**Idea:** Build a trie of roots and walk each sentence word until a terminal root or missing branch.
```python
from typing import List


class TrieNode:
    def __init__(self):
        self.children = {}
        self.word = None


class Solution:
    def replaceWords(self, dictionary: List[str], sentence: str) -> str:
        root = TrieNode()
        for word in dictionary:
            node = root
            for ch in word:
                if ch not in node.children:
                    node.children[ch] = TrieNode()
                node = node.children[ch]
            node.word = word

        def replace(word: str) -> str:
            node = root
            for ch in word:
                if node.word is not None:
                    return node.word
                if ch not in node.children:
                    return word
                node = node.children[ch]
            return node.word if node.word is not None else word

        return " ".join(replace(word) for word in sentence.split())
```
- **Time:** O(total root length + sentence characters) — **Space:** O(total trie nodes + S)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(S · L²) | O(R + S) |
| Better | O(S · R · L) | O(R + S) |
| Optimal | O(total root length + sentence characters) | O(total trie nodes + S) |

## Edge Cases & Pitfalls
- Always choose the shortest root, not the longest.
- Keep words unchanged when no prefix root exists.
- Check terminal roots before moving farther down the trie.

## Related
- Implement Trie
- Search Suggestions System
