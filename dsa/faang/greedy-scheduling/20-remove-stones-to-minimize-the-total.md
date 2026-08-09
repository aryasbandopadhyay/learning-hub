# 20. Remove Stones to Minimize the Total

- **Difficulty:** Medium
- **Pattern:** greedy scheduling
- **Asked at:** Amazon, Google, Microsoft

## Problem
Implement `minStoneSum` for **Remove Stones to Minimize the Total**. You are given piles of stones in `piles`. In one operation, choose any pile and remove `floor(pile / 2)` stones from it, leaving `ceil(pile / 2)` stones. Perform exactly `k` operations and return the minimum possible total number of stones remaining.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `piles`: list; stone piles.
- `k`: integer; required count, rank, or operation limit as defined above.

**Output**
- A single integer.

## Constraints
- `1 <= len(piles) <= 10^5`, `1 <= piles[i] <= 10^4`, and `1 <= k <= 10^5`

## Examples
```text
Input: piles = [5,4,9], k = 2
Output: 12
Explanation: Reduce 9 to 5, then reduce 5 to 3, for a total of 3 + 4 + 5 = 12. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Each operation removes the most stones when applied to the current largest pile. After reducing a pile, it may still be large enough to be chosen again later. A max-heap efficiently maintains the pile that gives the best immediate reduction.

## Approach 1 — Naive / Brute Force
**Idea:** For each operation, scan all piles to find the current largest pile and reduce it.
```python
class Solution:
    def minStoneSum(self, piles: list[int], k: int) -> int:
        piles = piles[:]
        for _ in range(k):
            best = 0
            for i in range(1, len(piles)):
                if piles[i] > piles[best]:
                    best = i
            piles[best] -= piles[best] // 2
        return sum(piles)
```
- **Time:** O(kn) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Maintain the piles in sorted order and reinsert the reduced largest pile after each operation.
```python
class Solution:
    def minStoneSum(self, piles: list[int], k: int) -> int:
        import bisect

        ordered = sorted(piles)
        for _ in range(k):
            largest = ordered.pop()
            largest -= largest // 2
            bisect.insort(ordered, largest)
        return sum(ordered)
```
- **Time:** O(n log n + kn) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a max-heap, represented by negated values, to reduce the largest pile in logarithmic time.
```python
class Solution:
    def minStoneSum(self, piles: list[int], k: int) -> int:
        import heapq

        heap = [-pile for pile in piles]
        heapq.heapify(heap)
        total = sum(piles)
        for _ in range(k):
            pile = -heapq.heappop(heap)
            removed = pile // 2
            pile -= removed
            total -= removed
            heapq.heappush(heap, -pile)
        return total
```
- **Time:** O(n + k log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(kn) | O(n) |
| Better | O(n log n + kn) | O(n) |
| Optimal | O(n + k log n) | O(n) |

## Edge Cases & Pitfalls
- Operations are exactly `k`, even if the largest pile is `1`.
- Removing `floor(pile / 2)` leaves `ceil(pile / 2)`.
- Reinsert the reduced pile before the next operation.

## Related
- Last Stone Weight
- Maximum Bags With Full Capacity of Rocks
