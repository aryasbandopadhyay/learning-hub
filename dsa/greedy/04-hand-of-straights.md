# 04. Hand of Straights

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Google, Amazon, Meta, Apple

## Problem
Given a hand of cards and a `groupSize`, determine whether the cards can be rearranged into groups
where each group has exactly `groupSize` cards with consecutive values. Every card must be used once.

**Input**
- `hand`: a list of integer card values.
- `groupSize`: the required size of each consecutive group.

**Output**
- A boolean: `True` if such a grouping is possible, otherwise `False`.

## Constraints
- 1 <= hand.length <= 10^4
- 0 <= hand[i] <= 10^9
- 1 <= groupSize <= hand.length

## Examples
```text
Input: hand = [1,2,3,6,2,3,4,7,8], groupSize = 3
Output: True
Explanation: The cards can be grouped as `[1,2,3]`, `[2,3,4]`, and `[6,7,8]`, so every card belongs to a consecutive group of size `3`.
```

## Understanding & Intuition
The smallest remaining card must start a group because no lower card can precede it. Greedily consume that card and the next `groupSize - 1` values. If any needed card is missing, no valid grouping exists.

## Approach 1 — Naive / Brute Force
**Idea:** Sort the cards, repeatedly pick the first unused card, and search for each next needed card.
```python
from typing import List

class Solution:
    def isNStraightHand(self, hand: List[int], groupSize: int) -> bool:
        if len(hand) % groupSize:
            return False
        cards = sorted(hand)
        used = [False] * len(cards)

        for i, card in enumerate(cards):
            if used[i]:
                continue
            used[i] = True
            need = card + 1
            for _ in range(groupSize - 1):
                found = False
                for j in range(len(cards)):
                    if not used[j] and cards[j] == need:
                        used[j] = True
                        need += 1
                        found = True
                        break
                if not found:
                    return False
        return True
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count cards and process distinct card values in sorted order.
```python
from collections import Counter
from typing import List

class Solution:
    def isNStraightHand(self, hand: List[int], groupSize: int) -> bool:
        if len(hand) % groupSize:
            return False
        count = Counter(hand)

        for card in sorted(count):
            copies = count[card]
            if copies == 0:
                continue
            for value in range(card, card + groupSize):
                if count[value] < copies:
                    return False
                count[value] -= copies
        return True
```
- **Time:** O(n log n + nk) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the same greedy start rule with counts; sorting dominates and each group position is touched only as needed.
```python
from collections import Counter
from typing import List

class Solution:
    def isNStraightHand(self, hand: List[int], groupSize: int) -> bool:
        if len(hand) % groupSize:
            return False
        count = Counter(hand)

        for start in sorted(count):
            while count[start] > 0:
                for card in range(start, start + groupSize):
                    if count[card] == 0:
                        return False
                    count[card] -= 1
        return True
```
- **Time:** O(n log n + n * groupSize in worst case) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n + nk) | O(n) |
| Optimal | O(n log n + nk) | O(n) |

## Edge Cases & Pitfalls
- If `len(hand)` is not divisible by `groupSize`, return `False`.
- Duplicates must be consumed carefully with counts.
- The smallest unconsumed card cannot be placed in the middle of a group.

## Related
- Divide Array in Sets of K Consecutive Numbers
- Sort and Count Greedy
