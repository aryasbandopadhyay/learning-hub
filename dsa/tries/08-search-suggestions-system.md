# 08. Search Suggestions System

- **Difficulty:** Medium
- **Pattern:** Tries
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given product names and a `searchWord`, return after each typed character up to three lexicographically smallest products that start with the current prefix. Product names and `searchWord` contain lowercase English letters.

## Examples
```text
Input: products = ["mobile","mouse","moneypot","monitor","mousepad"], searchWord = "mouse"
Output: [["mobile","moneypot","monitor"],["mobile","moneypot","monitor"],["mouse","mousepad"],["mouse","mousepad"],["mouse","mousepad"]]
Explanation: Suggestions are recomputed for prefixes "m", "mo", "mou", "mous", and "mouse".
```

## Understanding & Intuition
Each prefix needs the first three lexicographic products matching it. Sorting products once makes the order stable and easy to reuse. A trie can store the top three products at every prefix node during insertion.

## Approach 1 — Naive / Brute Force
**Idea:** For each prefix, scan all sorted products and take the first three matches.
```python
from typing import List


class Solution:
    def suggestedProducts(self, products: List[str], searchWord: str) -> List[List[str]]:
        products.sort()
        ans = []
        prefix = ""
        for ch in searchWord:
            prefix += ch
            suggestions = []
            for product in products:
                if product.startswith(prefix):
                    suggestions.append(product)
                    if len(suggestions) == 3:
                        break
            ans.append(suggestions)
        return ans
```
- **Time:** O(P log P · L + S · P · L) — **Space:** O(S)

## Approach 2 — Better
**Idea:** Sort products and use binary search to find the matching range for each prefix.
```python
from bisect import bisect_left
from typing import List


class Solution:
    def suggestedProducts(self, products: List[str], searchWord: str) -> List[List[str]]:
        products.sort()
        ans = []
        prefix = ""
        start = 0
        for ch in searchWord:
            prefix += ch
            start = bisect_left(products, prefix, start)
            suggestions = []
            # Only the first three candidates after lower_bound can matter.
            for i in range(start, min(start + 3, len(products))):
                if products[i].startswith(prefix):
                    suggestions.append(products[i])
            ans.append(suggestions)
        return ans
```
- **Time:** O(P log P · L + S(log P + L)) — **Space:** O(S)

## Approach 3 — Optimal
**Idea:** Insert sorted products into a trie and store up to three suggestions per prefix node.
```python
from typing import List


class TrieNode:
    def __init__(self):
        self.children = {}
        self.suggestions = []


class Solution:
    def suggestedProducts(self, products: List[str], searchWord: str) -> List[List[str]]:
        root = TrieNode()
        for product in sorted(products):
            node = root
            for ch in product:
                if ch not in node.children:
                    node.children[ch] = TrieNode()
                node = node.children[ch]
                if len(node.suggestions) < 3:
                    node.suggestions.append(product)

        ans = []
        node = root
        for ch in searchWord:
            if node is not None and ch in node.children:
                node = node.children[ch]
                ans.append(list(node.suggestions))
            else:
                node = None
                ans.append([])
        return ans
```
- **Time:** O(P log P · L + total product characters + S) — **Space:** O(total trie nodes)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(P log P · L + S · P · L) | O(S) |
| Better | O(P log P · L + S(log P + L)) | O(S) |
| Optimal | O(P log P · L + total product characters + S) | O(total trie nodes) |

## Edge Cases & Pitfalls
- Suggestions must be lexicographically sorted.
- Return an empty list for every later prefix after the trie path breaks.
- Limit every suggestion list to at most three products.

## Related
- Replace Words
- Autocomplete systems
