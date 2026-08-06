# 04. Minimize Max Distance to Gas Station

- **Difficulty:** Hard
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You are given sorted station positions `stations` and an integer `k`. Add exactly `k` new stations anywhere on the number line to minimize the maximum distance between adjacent stations. Return the minimized maximum distance rounded to 6 decimal places.

Constraints: `2 <= len(stations) <= 2000`, `0 <= k <= 10^6`, `0 <= stations[i] <= 10^8`, positions are strictly increasing.

## Examples
```text
Input: stations = [1,2,3,4,5,6,7,8,9,10], k = 9
Output: 0.5
Explanation: Placing one station in every unit gap makes each largest adjacent distance 0.5.
```

## Understanding & Intuition
For a proposed maximum gap `d`, each original gap independently needs a known number of extra stations. The feasibility predicate, needing at most `k` stations, is monotonic as `d` grows.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly split the currently largest segment using a heap and track how many parts each original gap has.
```python
class Solution:
    def minmaxGasDist(self, stations: list[int], k: int) -> float:
        import heapq
        gaps = [stations[i + 1] - stations[i] for i in range(len(stations) - 1)]
        parts = [1] * len(gaps)
        heap = [(-g, i) for i, g in enumerate(gaps)]
        heapq.heapify(heap)
        for _ in range(k):
            _, i = heapq.heappop(heap)
            parts[i] += 1
            heapq.heappush(heap, (-(gaps[i] / parts[i]), i))
        return round(-heap[0][0], 6)
```
- **Time:** O((n + k) log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Binary search with a fixed number of iterations over floating-point answers.
```python
class Solution:
    def minmaxGasDist(self, stations: list[int], k: int) -> float:
        import math
        lo, hi = 0.0, float(stations[-1] - stations[0])
        for _ in range(70):
            mid = (lo + hi) / 2.0
            need = 0
            for i in range(len(stations) - 1):
                gap = stations[i + 1] - stations[i]
                need += int(math.ceil(gap / mid)) - 1
            if need <= k:
                hi = mid
            else:
                lo = mid
        return round(hi, 6)
```
- **Time:** O(n log P) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search to a numeric tolerance and compute required stations using integer flooring with an exact-divisibility correction.
```python
class Solution:
    def minmaxGasDist(self, stations: list[int], k: int) -> float:
        lo, hi = 0.0, float(stations[-1] - stations[0])
        while hi - lo > 1e-7:
            mid = (lo + hi) / 2.0
            need = 0
            for i in range(len(stations) - 1):
                gap = stations[i + 1] - stations[i]
                q = gap / mid
                need += int(q)
                if abs(q - int(q)) < 1e-12:
                    need -= 1
            if need <= k:
                hi = mid
            else:
                lo = mid
        return round(hi, 6)
```
- **Time:** O(n log((max gap)/eps)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((n + k) log n) | O(n) |
| Better | O(n log P) | O(1) |
| Optimal | O(n log((max gap)/eps)) | O(1) |

## Edge Cases & Pitfalls
- Return a rounded float for deterministic output.
- Exact divisibility should not require an extra station.
- `k = 0` returns the original largest gap.

## Related
- Koko Eating Bananas
- Split Array Largest Sum
