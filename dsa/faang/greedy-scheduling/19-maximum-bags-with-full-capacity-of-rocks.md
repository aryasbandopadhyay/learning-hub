# 19. Maximum Bags With Full Capacity of Rocks

- **Difficulty:** Medium
- **Pattern:** greedy scheduling
- **Asked at:** Amazon, Google, Microsoft

## Problem
You have bags with maximum `capacity`, current `rocks`, and `additionalRocks` extra rocks. Return the maximum number of bags that can be filled to capacity after distributing at most `additionalRocks` rocks.

Constraints: `1 <= len(capacity) == len(rocks) <= 5 * 10^4`, `1 <= rocks[i] <= capacity[i] <= 10^9`, and `0 <= additionalRocks <= 10^9`.

## Examples
```text
Input: capacity = [2,3,4,5], rocks = [1,2,4,4], additionalRocks = 2
Output: 3
Explanation: Fill the first two bags; the third bag is already full.
```

## Understanding & Intuition
Each bag has a gap equal to `capacity[i] - rocks[i]`. To maximize the number of full bags, spend rocks on the smallest gaps first. Any solution filling a larger gap before a smaller one can swap those choices and fill at least as many bags.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan for the non-full bag with the smallest remaining gap and fill it if possible.
```python
class Solution:
    def maximumBags(self, capacity: list[int], rocks: list[int], additionalRocks: int) -> int:
        gaps = [capacity[i] - rocks[i] for i in range(len(capacity))]
        full = sum(1 for gap in gaps if gap == 0)
        used = [gap == 0 for gap in gaps]
        while True:
            best = -1
            for i, gap in enumerate(gaps):
                if not used[i] and (best == -1 or gap < gaps[best]):
                    best = i
            if best == -1 or gaps[best] > additionalRocks:
                return full
            additionalRocks -= gaps[best]
            used[best] = True
            full += 1
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Put all positive gaps in a min-heap and fill bags by repeatedly taking the smallest gap.
```python
class Solution:
    def maximumBags(self, capacity: list[int], rocks: list[int], additionalRocks: int) -> int:
        import heapq

        full = 0
        heap = []
        for cap, rock in zip(capacity, rocks):
            gap = cap - rock
            if gap == 0:
                full += 1
            else:
                heapq.heappush(heap, gap)
        while heap and heap[0] <= additionalRocks:
            additionalRocks -= heapq.heappop(heap)
            full += 1
        return full
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort all gaps and greedily pay the smallest gaps first.
```python
class Solution:
    def maximumBags(self, capacity: list[int], rocks: list[int], additionalRocks: int) -> int:
        gaps = sorted(cap - rock for cap, rock in zip(capacity, rocks))
        full = 0
        for gap in gaps:
            if gap <= additionalRocks:
                additionalRocks -= gap
                full += 1
            else:
                break
        return full
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Already full bags have gap `0` and count immediately.
- You may leave rocks unused.
- Filling a smaller gap first can never reduce the final count.

## Related
- Maximum Ice Cream Bars
- Assign Cookies
