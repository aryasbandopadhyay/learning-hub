# 11. Sort Characters By Frequency

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Given a string `s`, rearrange its characters by non-increasing frequency. Characters that occur more often must appear before less frequent characters; ties may be in any order.

**Input**
- `s`: the string to sort by character frequency.

**Output**
- A string containing exactly the characters of `s`, grouped by character and ordered by descending frequency. The judge accepts any valid tie order.

## Constraints
- `1 <= s.length <= 5 * 10^5`
- `s` contains uppercase letters, lowercase letters, and digits.

## Examples
```text
Input: s = "tree"
Output: "eert"
Explanation: `e` appears twice, while `t` and `r` appear once, so both `e` characters come first.
```

## Understanding & Intuition
Frequency counting tells how many times each character must appear. Sorting the unique characters by count is usually simple enough. A max-heap is an alternative priority-queue pattern that emits highest-frequency characters first.

## Approach 1 — Naive / Brute Force
**Idea:** Count each unique character by scanning the string, then sort by count.
```python
class Solution:
    def frequencySort(self, s: str) -> str:
        parts = []
        for ch in set(s):
            # Re-counting for every unique character is brute force.
            parts.append((s.count(ch), ch))
        parts.sort(reverse=True)
        return "".join(ch * count for count, ch in parts)
```
- **Time:** O(nu + u log u) — **Space:** O(u)

## Approach 2 — Better
**Idea:** Use `Counter` and sort unique characters by frequency descending.
```python
from collections import Counter

class Solution:
    def frequencySort(self, s: str) -> str:
        counts = Counter(s)
        chars = sorted(counts, key=lambda ch: counts[ch], reverse=True)
        return "".join(ch * counts[ch] for ch in chars)
```
- **Time:** O(n + u log u) — **Space:** O(u)

## Approach 3 — Optimal
**Idea:** Push `(-count, ch)` into a heap and append chunks as they are popped.
```python
from collections import Counter
import heapq

class Solution:
    def frequencySort(self, s: str) -> str:
        heap = [(-count, ch) for ch, count in Counter(s).items()]
        heapq.heapify(heap)
        result = []

        while heap:
            neg_count, ch = heapq.heappop(heap)
            result.append(ch * (-neg_count))

        return "".join(result)
```
- **Time:** O(n + u log u) — **Space:** O(u)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nu + u log u) | O(u) |
| Better | O(n + u log u) | O(u) |
| Optimal | O(n + u log u) | O(u) |

## Edge Cases & Pitfalls
- Equal-frequency characters can appear in any order.
- Build output in chunks instead of repeated string concatenation.
- Large strings require O(n) output space regardless of method.

## Related
- Top K Frequent Words
- Reorganize String
- Sort Array By Increasing Frequency
