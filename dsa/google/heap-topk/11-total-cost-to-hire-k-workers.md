# 11. Total Cost to Hire K Workers

- **Difficulty:** Medium
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given `costs`, hire exactly `k` workers. In each hiring session, you may choose from the first `candidates` remaining workers and the last `candidates` remaining workers; hire the worker with the lower cost, breaking ties by smaller original index. Return the total cost. Constraints: `1 <= k <= len(costs) <= 100000`.

## Examples
```text
Input: costs = [17,12,10,2,7,2,11,20,8], k = 3, candidates = 4
Output: 11
Explanation: Hire costs 2, 2, and 7 under the candidate-window rules.
```

## Understanding & Intuition
Only workers at the two active ends can be considered. After hiring from one side, the window on that side advances by one. Heaps make it efficient to pick the cheapest eligible worker while preserving the index tie-break.

## Approach 1 — Naive / Brute Force
**Idea:** Maintain remaining original indices, rebuild the eligible set every session, and pick the minimum ranked worker.
```python
class Solution:
    def totalCost(self, costs: list[int], k: int, candidates: int) -> int:
        remaining = list(range(len(costs)))
        total = 0
        for _ in range(k):
            eligible = remaining[:candidates] + remaining[max(candidates, len(remaining) - candidates):]
            idx = min(eligible, key=lambda i: (costs[i], i))
            total += costs[idx]
            remaining.remove(idx)
        return total
```
- **Time:** O(kn) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep two sorted candidate lists for left and right windows and refill the side that was hired.
```python
class Solution:
    def totalCost(self, costs: list[int], k: int, candidates: int) -> int:
        import bisect
        n = len(costs)
        left, right = 0, n - 1
        left_pool = []
        right_pool = []
        while left <= right and len(left_pool) < candidates:
            bisect.insort(left_pool, (costs[left], left))
            left += 1
        while left <= right and len(right_pool) < candidates:
            bisect.insort(right_pool, (costs[right], right))
            right -= 1
        total = 0
        for _ in range(k):
            take_left = right_pool == [] or (left_pool and left_pool[0] <= right_pool[0])
            if take_left:
                cost, idx = left_pool.pop(0)
                if left <= right:
                    bisect.insort(left_pool, (costs[left], left))
                    left += 1
            else:
                cost, idx = right_pool.pop(0)
                if left <= right:
                    bisect.insort(right_pool, (costs[right], right))
                    right -= 1
            total += cost
        return total
```
- **Time:** O(kc) — **Space:** O(c)

## Approach 3 — Optimal
**Idea:** Use two min-heaps, one for each side's candidate pool, and refill from the side that supplied the hired worker.
```python
class Solution:
    def totalCost(self, costs: list[int], k: int, candidates: int) -> int:
        import heapq
        n = len(costs)
        left, right = 0, n - 1
        left_heap = []
        right_heap = []
        while left <= right and len(left_heap) < candidates:
            heapq.heappush(left_heap, (costs[left], left))
            left += 1
        while left <= right and len(right_heap) < candidates:
            heapq.heappush(right_heap, (costs[right], right))
            right -= 1
        total = 0
        for _ in range(k):
            take_left = not right_heap or (left_heap and left_heap[0] <= right_heap[0])
            if take_left:
                cost, _ = heapq.heappop(left_heap)
                if left <= right:
                    heapq.heappush(left_heap, (costs[left], left))
                    left += 1
            else:
                cost, _ = heapq.heappop(right_heap)
                if left <= right:
                    heapq.heappush(right_heap, (costs[right], right))
                    right -= 1
            total += cost
        return total
```
- **Time:** O((k + candidates) log candidates) — **Space:** O(candidates)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(kn) | O(n) |
| Better | O(kc) | O(c) |
| Optimal | O((k + candidates) log candidates) | O(candidates) |

## Edge Cases & Pitfalls
- Candidate windows can overlap; avoid adding the same worker twice.
- Ties are by original index, not current position.
- Refill only from the side that was hired.

## Related
- IPO
- K Pairs with Smallest Sums
