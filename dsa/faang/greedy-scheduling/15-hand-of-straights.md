# 15. Hand of Straights

- **Difficulty:** Medium
- **Pattern:** greedy scheduling
- **Asked at:** Google, Amazon, Facebook

## Problem
Given a list of card values `hand` and an integer `groupSize`, return `True` if the cards can be rearranged into groups of size `groupSize` where each group contains consecutive values. Otherwise return `False`.

Constraints: `1 <= len(hand) <= 10^4`, `0 <= hand[i] <= 10^9`, and `1 <= groupSize <= len(hand)`.

## Examples
```text
Input: hand = [1,2,3,6,2,3,4,7,8], groupSize = 3
Output: True
Explanation: The hand can be split into [1, 2, 3], [2, 3, 4], and [6, 7, 8].
```

## Understanding & Intuition
The smallest remaining card must be the start of some consecutive group. Once that card is chosen, the next `groupSize - 1` values are forced. Greedily forming groups from the smallest value avoids wasting cards that cannot appear later.

## Approach 1 — Naive / Brute Force
**Idea:** Keep a sorted list and repeatedly remove the smallest card plus the next required consecutive values.
```python
class Solution:
    def isNStraightHand(self, hand: list[int], groupSize: int) -> bool:
        if len(hand) % groupSize != 0:
            return False
        cards = sorted(hand)
        while cards:
            start = cards.pop(0)
            for value in range(start + 1, start + groupSize):
                try:
                    idx = cards.index(value)
                except ValueError:
                    return False
                cards.pop(idx)
        return True
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count cards, scan sorted distinct values, and consume as many groups as the current smallest remaining count requires.
```python
class Solution:
    def isNStraightHand(self, hand: list[int], groupSize: int) -> bool:
        from collections import Counter

        if len(hand) % groupSize != 0:
            return False
        count = Counter(hand)
        for value in sorted(count):
            need = count[value]
            if need == 0:
                continue
            for nxt in range(value, value + groupSize):
                if count[nxt] < need:
                    return False
                count[nxt] -= need
        return True
```
- **Time:** O(n log n + ng) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a min-heap of card values so the current smallest remaining card always starts the next group.
```python
class Solution:
    def isNStraightHand(self, hand: list[int], groupSize: int) -> bool:
        from collections import Counter
        import heapq

        if len(hand) % groupSize != 0:
            return False
        count = Counter(hand)
        heap = list(count)
        heapq.heapify(heap)
        while heap:
            start = heap[0]
            for value in range(start, start + groupSize):
                if count[value] == 0:
                    return False
                count[value] -= 1
                if count[value] == 0:
                    if value != heap[0]:
                        return False
                    heapq.heappop(heap)
        return True
```
- **Time:** O(n log n + ng) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n + ng) | O(n) |
| Optimal | O(n log n + ng) | O(n) |

## Edge Cases & Pitfalls
- If `len(hand)` is not divisible by `groupSize`, the answer is immediately false.
- Duplicate cards are allowed and must be counted carefully.
- Never start a group above the smallest remaining card.

## Related
- Divide Array in Sets of K Consecutive Numbers
- Split Array into Consecutive Subsequences
