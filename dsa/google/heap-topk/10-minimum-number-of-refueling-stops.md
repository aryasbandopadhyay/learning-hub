# 10. Minimum Number of Refueling Stops

- **Difficulty:** Hard
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
A car starts at position `0` with `startFuel` liters and needs to reach `target`. Each station is `[position, fuel]`, sorted by position, and refueling takes all fuel from a station. Return the minimum number of stops needed, or `-1` if impossible.

Implement `Solution.minRefuelStops` with the parameters below and return the requested value.

**Input**
- `target`: a `int`; the destination position.
- `startFuel`: a `int`; the fuel available at position `0`.
- `stations`: a `list[list[int]]`; fuel stations as `[position, fuel]` pairs sorted by position.

**Output**
- The minimum number of refueling stops needed to reach `target`, or `-1` if the trip is impossible.

## Constraints
- `1 <= target <= 10^9`, `0 <= len(stations) <= 500`

## Examples
```text
Input: target = 100, startFuel = 10, stations = [[10,60],[20,30],[30,30],[60,40]]
Output: 2
Explanation: Stop at positions 10 and 60 to reach the target.
```

## Understanding & Intuition
When you cannot reach the next position, the best past station to use is the one with the most fuel. This greedy choice can be delayed until fuel is actually needed. A max-heap stores fuels from stations already passed.

## Approach 1 — Naive / Brute Force
**Idea:** Try all subsets of stations by dynamic programming over how far each stop count can reach.
```python
class Solution:
    def minRefuelStops(self, target: int, startFuel: int, stations: list[list[int]]) -> int:
        n = len(stations)
        dp = [startFuel] + [-1] * n
        for i, (pos, fuel) in enumerate(stations):
            for stops in range(i, -1, -1):
                if dp[stops] >= pos:
                    dp[stops + 1] = max(dp[stops + 1], dp[stops] + fuel)
        for stops, reach in enumerate(dp):
            if reach >= target:
                return stops
        return -1
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Binary search the stop count and use DP to test whether that many stops can reach the target.
```python
class Solution:
    def minRefuelStops(self, target: int, startFuel: int, stations: list[list[int]]) -> int:
        def can(limit):
            dp = [startFuel] + [-1] * limit
            for pos, fuel in stations:
                for stops in range(limit - 1, -1, -1):
                    if dp[stops] >= pos:
                        dp[stops + 1] = max(dp[stops + 1], dp[stops] + fuel)
            return max(dp) >= target
        if startFuel >= target:
            return 0
        lo, hi, ans = 1, len(stations), -1
        while lo <= hi:
            mid = (lo + hi) // 2
            if can(mid):
                ans = mid
                hi = mid - 1
            else:
                lo = mid + 1
        return ans
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sweep stations and the target; whenever the next position is unreachable, refuel from the largest passed station.
```python
class Solution:
    def minRefuelStops(self, target: int, startFuel: int, stations: list[list[int]]) -> int:
        import heapq
        fuel = startFuel
        prev = 0
        stops = 0
        heap = []
        for pos, amount in stations + [[target, 0]]:
            fuel -= pos - prev
            while fuel < 0 and heap:
                fuel += -heapq.heappop(heap)
                stops += 1
            if fuel < 0:
                return -1
            heapq.heappush(heap, -amount)
            prev = pos
        return stops
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2 log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Stations can be skipped even if reachable.
- Add the target as a final checkpoint with zero fuel.
- The heap stores passed stations only, never future stations.

## Related
- Furthest Building You Can Reach
- Jump Game
