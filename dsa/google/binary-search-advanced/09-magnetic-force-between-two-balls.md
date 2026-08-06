# 09. Magnetic Force Between Two Balls

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given basket positions `position` and an integer `m`, place `m` balls into distinct baskets to maximize the minimum distance between any two balls. Return that largest possible minimum distance.

Constraints: `2 <= len(position) <= 10^5`, `2 <= m <= len(position)`, `1 <= position[i] <= 10^9`, positions are distinct.

## Examples
```text
Input: position = [1,2,3,4,7], m = 3
Output: 3
Explanation: Placing balls at 1, 4, and 7 gives minimum distance 3.
```

## Understanding & Intuition
For a candidate distance, greedily placing each next ball at the earliest possible basket maximizes remaining space. If distance `d` is feasible, every smaller distance is feasible too.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible distance from large to small and greedily test placement.
```python
class Solution:
    def maxDistance(self, position: list[int], m: int) -> int:
        position.sort()
        for dist in range(position[-1] - position[0], 0, -1):
            count = 1
            last = position[0]
            for p in position[1:]:
                if p - last >= dist:
                    count += 1
                    last = p
            if count >= m:
                return dist
        return 0
```
- **Time:** O(nR) — **Space:** O(1), where `R` is the coordinate range

## Approach 2 — Better
**Idea:** Binary search feasible distances with a simple greedy checker.
```python
class Solution:
    def maxDistance(self, position: list[int], m: int) -> int:
        position.sort()
        lo, hi = 1, position[-1] - position[0]
        ans = 0
        while lo <= hi:
            mid = (lo + hi) // 2
            count = 1
            last = position[0]
            for p in position[1:]:
                if p - last >= mid:
                    count += 1
                    last = p
            if count >= m:
                ans = mid
                lo = mid + 1
            else:
                hi = mid - 1
        return ans
```
- **Time:** O(n log R) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use upper-mid binary search for the maximum feasible distance and stop greedy checks early.
```python
class Solution:
    def maxDistance(self, position: list[int], m: int) -> int:
        position.sort()
        lo, hi = 1, position[-1] - position[0]
        while lo < hi:
            mid = (lo + hi + 1) // 2
            count = 1
            last = position[0]
            for p in position[1:]:
                if p - last >= mid:
                    count += 1
                    last = p
                    if count == m:
                        break
            if count >= m:
                lo = mid
            else:
                hi = mid - 1
        return lo
```
- **Time:** O(n log R) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nR) | O(1) |
| Better | O(n log R) | O(1) |
| Optimal | O(n log R) | O(1) |

## Edge Cases & Pitfalls
- Sort positions before checking distances.
- Use upper-mid when searching for a maximum feasible value.
- Greedy earliest placement is sufficient for feasibility.

## Related
- Aggressive Cows
- Minimize Max Distance to Gas Station
