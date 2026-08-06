# 03. Last Stone Weight

- **Difficulty:** Easy
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Apple, Bloomberg

## Problem
You are given stones with positive integer weights. Each turn, choose the two heaviest stones `x <= y`; if equal, both are destroyed, otherwise `y - x` is inserted back. Return the final stone weight, or `0` if no stones remain. Constraints: `1 <= len(stones) <= 30`, `1 <= stones[i] <= 1000`.

## Examples
```text
Input: stones = [2,7,4,1,8,1]
Output: 1
Explanation: Smash 8 and 7 -> 1, then eventually one stone of weight 1 remains.
```

## Understanding & Intuition
Every operation needs the current two largest stones. Sorting repeatedly works but repeats a lot of ordering work. A max-priority queue gives direct access to the heaviest stones after every update.

## Approach 1 — Naive / Brute Force
**Idea:** Re-sort the list before each smash and remove the last two values.
```python
from typing import List

class Solution:
    def lastStoneWeight(self, stones: List[int]) -> int:
        stones = stones[:]
        while len(stones) > 1:
            stones.sort()
            x = stones.pop()
            y = stones.pop()
            if x != y:
                stones.append(x - y)
        return stones[0] if stones else 0
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep the list sorted and insert the new difference with binary search.
```python
from typing import List
import bisect

class Solution:
    def lastStoneWeight(self, stones: List[int]) -> int:
        stones = sorted(stones)
        while len(stones) > 1:
            x = stones.pop()
            y = stones.pop()
            if x != y:
                # Maintain sorted order after the smash.
                bisect.insort(stones, x - y)
        return stones[0] if stones else 0
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use Python's min-heap with negative weights to simulate a max-heap.
```python
from typing import List
import heapq

class Solution:
    def lastStoneWeight(self, stones: List[int]) -> int:
        heap = [-stone for stone in stones]
        heapq.heapify(heap)

        while len(heap) > 1:
            first = -heapq.heappop(heap)
            second = -heapq.heappop(heap)
            if first != second:
                heapq.heappush(heap, -(first - second))

        return -heap[0] if heap else 0
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Equal stones both disappear.
- One initial stone should be returned directly.
- When using negative weights, remember to negate both popped values and the pushed difference.

## Related
- Last Stone Weight II
- Kth Largest Element in a Stream
- Minimum Cost to Connect Sticks
