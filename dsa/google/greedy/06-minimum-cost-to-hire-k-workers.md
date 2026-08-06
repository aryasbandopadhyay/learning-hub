# 06. Minimum Cost to Hire K Workers

- **Difficulty:** Hard
- **Pattern:** greedy
- **Asked at:** Google

## Problem
There are `n` workers with `quality[i]` and minimum wage expectation `wage[i]`. To hire exactly `k` workers, everyone must be paid in proportion to quality at one common rate, and each worker must receive at least their wage. Return the minimum total cost rounded to five decimal places.

Constraints: `1 <= k <= len(quality) <= 10^4`, `1 <= quality[i], wage[i] <= 10^4`.

## Examples
```text
Input: quality = [10,20,5], wage = [70,50,30], k = 2
Output: 105.0
Explanation: Hire workers 0 and 2 at rate 7.0 for total quality 15.
```

## Understanding & Intuition
For any chosen group, the common pay rate must be at least every worker's `wage / quality`, so it equals the maximum ratio in the group. If that maximum-ratio worker is fixed, the best partners are the lowest total qualities among workers with no larger ratio. Sorting by ratio reveals each possible maximum in order.

## Approach 1 — Naive / Brute Force
**Idea:** Treat each worker's ratio as the group rate and sort all eligible qualities to choose the cheapest `k`.
```python
class Solution:
    def mincostToHireWorkers(self, quality: list[int], wage: list[int], k: int) -> float:
        n = len(quality)
        best = float('inf')
        for i in range(n):
            rate = wage[i] / quality[i]
            eligible = []
            for q, w in zip(quality, wage):
                if w / q <= rate:
                    eligible.append(q)
            if len(eligible) >= k:
                eligible.sort()
                best = min(best, sum(eligible[:k]) * rate)
        return round(best, 5)
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort by ratio and maintain the previous qualities in sorted order to test each ratio.
```python
class Solution:
    def mincostToHireWorkers(self, quality, wage, k):
        import bisect
        workers = sorted((w / q, q) for q, w in zip(quality, wage))
        qualities = []
        best = float('inf')
        for rate, q in workers:
            bisect.insort(qualities, q)
            if len(qualities) >= k:
                total_q = sum(qualities[:k])
                best = min(best, total_q * rate)
        return round(best, 5)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by ratio and use a max-heap to keep only the `k` smallest qualities seen so far.
```python
class Solution:
    def mincostToHireWorkers(self, quality, wage, k):
        import heapq
        workers = sorted((w / q, q) for q, w in zip(quality, wage))
        heap = []
        total_q = 0
        best = float('inf')
        for rate, q in workers:
            heapq.heappush(heap, -q)
            total_q += q
            if len(heap) > k:
                total_q += heapq.heappop(heap)
            if len(heap) == k:
                best = min(best, total_q * rate)
        return round(best, 5)
```
- **Time:** O(n log n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(k) |

## Edge Cases & Pitfalls
- The highest ratio in a group determines the pay rate.
- Use floating-point division, not integer division.
- Rounding is applied only to the returned final answer.

## Related
- Two City Scheduling
- IPO
