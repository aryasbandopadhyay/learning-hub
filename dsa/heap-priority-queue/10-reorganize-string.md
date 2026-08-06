# 10. Reorganize String

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a string `s`, rearrange its characters so no two adjacent characters are the same. Return any valid rearrangement, or `""` if impossible. Constraints: `1 <= len(s) <= 500`, `s` contains lowercase English letters.

## Examples
```text
Input: s = "aab"
Output: "aba"
Explanation: No adjacent characters are equal.
```

## Understanding & Intuition
The most frequent character is the only one that can make the task impossible. Greedy placement should repeatedly use the most common character that is not equal to the previous output. A max-heap plus one-step cooldown implements that choice cleanly.

## Approach 1 — Naive / Brute Force
**Idea:** Backtrack over all character choices until a valid permutation is found.
```python
from collections import Counter

class Solution:
    def reorganizeString(self, s: str) -> str:
        counts = Counter(s)
        result = []

        def backtrack(prev: str) -> bool:
            if len(result) == len(s):
                return True
            for ch in list(counts):
                if counts[ch] > 0 and ch != prev:
                    counts[ch] -= 1
                    result.append(ch)
                    if backtrack(ch):
                        return True
                    result.pop()
                    counts[ch] += 1
            return False

        return "".join(result) if backtrack("") else ""
```
- **Time:** O(n!) worst — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort characters by frequency and place them into even indices, then odd indices.
```python
from collections import Counter

class Solution:
    def reorganizeString(self, s: str) -> str:
        n = len(s)
        counts = Counter(s)
        if max(counts.values()) > (n + 1) // 2:
            return ""

        chars = sorted(counts, key=lambda ch: -counts[ch])
        result = [""] * n
        index = 0
        for ch in chars:
            for _ in range(counts[ch]):
                if index >= n:
                    index = 1
                result[index] = ch
                index += 2
        return "".join(result)
```
- **Time:** O(n + u log u) — **Space:** O(n + u)

## Approach 3 — Optimal
**Idea:** Repeatedly pop the most frequent available character, holding the previous character out for one turn.
```python
from collections import Counter
import heapq

class Solution:
    def reorganizeString(self, s: str) -> str:
        counts = Counter(s)
        if max(counts.values()) > (len(s) + 1) // 2:
            return ""

        heap = [(-count, ch) for ch, count in counts.items()]
        heapq.heapify(heap)
        result = []
        prev_count, prev_ch = 0, ""

        while heap:
            count, ch = heapq.heappop(heap)
            result.append(ch)
            count += 1  # One occurrence was used.

            if prev_count < 0:
                heapq.heappush(heap, (prev_count, prev_ch))

            prev_count, prev_ch = count, ch

        return "".join(result)
```
- **Time:** O(n log u) — **Space:** O(u)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n!) worst | O(n) |
| Better | O(n + u log u) | O(n + u) |
| Optimal | O(n log u) | O(u) |

## Edge Cases & Pitfalls
- If a frequency exceeds `(n + 1) // 2`, no solution exists.
- The heap approach must not immediately reinsert the just-used character.
- Any valid answer is accepted.

## Related
- Task Scheduler
- Longest Happy String
- Rearrange String k Distance Apart
