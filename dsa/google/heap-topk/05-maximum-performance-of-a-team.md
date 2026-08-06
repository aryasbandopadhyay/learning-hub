# 05. Maximum Performance of a Team

- **Difficulty:** Hard
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
There are `n` engineers with `speed[i]` and `efficiency[i]`. Choose at most `k` engineers to maximize `(sum of chosen speeds) * (minimum chosen efficiency)`, and return the value modulo `1000000007`. Constraints: `1 <= k <= n <= 100000`.

## Examples
```text
Input: n = 6, speed = [2,10,3,1,5,8], efficiency = [5,4,3,9,7,2], k = 2
Output: 60
Explanation: Engineers with speeds 10 and 5 have minimum efficiency 4, so performance is 15 * 4.
```

## Understanding & Intuition
If an engineer is the minimum-efficiency member, all other chosen engineers must have efficiency at least that value. After sorting by efficiency descending, the best team ending at the current engineer uses the largest speeds among engineers already seen. A min-heap maintains those top speeds.

## Approach 1 — Naive / Brute Force
**Idea:** For each engineer as the minimum-efficiency candidate, scan all eligible speeds and take the largest `k`.
```python
class Solution:
    def maxPerformance(self, n: int, speed: list[int], efficiency: list[int], k: int) -> int:
        mod = 1000000007
        best = 0
        for e in efficiency:
            eligible = [speed[i] for i in range(n) if efficiency[i] >= e]
            eligible.sort(reverse=True)
            best = max(best, sum(eligible[:k]) * e)
        return best % mod
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Process efficiencies descending and keep the selected speeds in sorted order, trimming the smallest when more than `k` are kept.
```python
class Solution:
    def maxPerformance(self, n: int, speed: list[int], efficiency: list[int], k: int) -> int:
        import bisect
        mod = 1000000007
        engineers = sorted(zip(efficiency, speed), reverse=True)
        chosen = []
        speed_sum = 0
        best = 0
        for e, s in engineers:
            bisect.insort(chosen, s)
            speed_sum += s
            if len(chosen) > k:
                speed_sum -= chosen.pop(0)
            best = max(best, speed_sum * e)
        return best % mod
```
- **Time:** O(nk) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Replace the sorted list with a min-heap of selected speeds.
```python
class Solution:
    def maxPerformance(self, n: int, speed: list[int], efficiency: list[int], k: int) -> int:
        import heapq
        mod = 1000000007
        engineers = sorted(zip(efficiency, speed), reverse=True)
        heap = []
        speed_sum = 0
        best = 0
        for e, s in engineers:
            heapq.heappush(heap, s)
            speed_sum += s
            if len(heap) > k:
                speed_sum -= heapq.heappop(heap)
            best = max(best, speed_sum * e)
        return best % mod
```
- **Time:** O(n log k) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(nk) | O(k) |
| Optimal | O(n log k) | O(k) |

## Edge Cases & Pitfalls
- The team may contain fewer than `k` engineers.
- Sort by efficiency descending so the current efficiency is the minimum.
- Take the modulo only after computing the maximum performance.

## Related
- Maximum Subsequence Score
- IPO
