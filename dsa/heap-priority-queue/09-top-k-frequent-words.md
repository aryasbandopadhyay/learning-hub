# 09. Top K Frequent Words

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a list of words and integer `k`, return the `k` most frequent words. Sort by frequency descending, and for equal frequency sort lexicographically ascending. Constraints: `1 <= len(words) <= 500`, `1 <= k <= unique words`, words contain lowercase letters.

## Examples
```text
Input: words = ["i","love","leetcode","i","love","coding"], k = 2
Output: ["i","love"]
Explanation: "i" and "love" both appear twice; "i" is lexicographically smaller.
```

## Understanding & Intuition
Counting frequencies is the core first step. Sorting unique words by the required order is simplest. A heap can also order candidates and pop the best `k` words efficiently when the unique set is large.

## Approach 1 — Naive / Brute Force
**Idea:** Count each unique word by scanning the full list, then sort.
```python
from typing import List

class Solution:
    def topKFrequent(self, words: List[str], k: int) -> List[str]:
        unique = set(words)
        counts = []
        for word in unique:
            # Re-scanning words for each unique word is the brute force cost.
            counts.append((words.count(word), word))

        counts.sort(key=lambda item: (-item[0], item[1]))
        return [word for _, word in counts[:k]]
```
- **Time:** O(nu + u log u) — **Space:** O(u)

## Approach 2 — Better
**Idea:** Use `Counter`, then sort unique words by `(-frequency, word)`.
```python
from typing import List
from collections import Counter

class Solution:
    def topKFrequent(self, words: List[str], k: int) -> List[str]:
        freq = Counter(words)
        ordered = sorted(freq.keys(), key=lambda word: (-freq[word], word))
        return ordered[:k]
```
- **Time:** O(n + u log u) — **Space:** O(u)

## Approach 3 — Optimal
**Idea:** Push `(-frequency, word)` into a min-heap; Python pops highest frequency and lexicographically smallest word first.
```python
from typing import List
from collections import Counter
import heapq

class Solution:
    def topKFrequent(self, words: List[str], k: int) -> List[str]:
        freq = Counter(words)
        heap = [(-count, word) for word, count in freq.items()]
        heapq.heapify(heap)

        answer = []
        for _ in range(k):
            answer.append(heapq.heappop(heap)[1])
        return answer
```
- **Time:** O(n + u + k log u) — **Space:** O(u)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nu + u log u) | O(u) |
| Better | O(n + u log u) | O(u) |
| Optimal | O(n + u + k log u) | O(u) |

## Edge Cases & Pitfalls
- Tie-break is lexicographically ascending, not insertion order.
- Do not return arbitrary heap order; pop exactly `k` entries.
- `k` is bounded by the number of unique words.

## Related
- Top K Frequent Elements
- Sort Characters By Frequency
- Kth Largest Element in an Array
