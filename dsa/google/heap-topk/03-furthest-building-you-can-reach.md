# 03. Furthest Building You Can Reach

- **Difficulty:** Medium
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given building `heights`, `bricks`, and `ladders`, return the furthest building index you can reach when moving left to right. Climbing up by `d` needs either `d` bricks or one ladder; descending or staying level costs nothing. Constraints: `1 <= len(heights) <= 100000`.

## Examples
```text
Input: heights = [4,2,7,6,9,14,12], bricks = 5, ladders = 1
Output: 4
Explanation: Use bricks for climb 5 and the ladder for climb 3, reaching index 4.
```

## Understanding & Intuition
Only positive climbs matter. To go as far as possible, ladders should cover the largest climbs seen so far and bricks should cover the rest. A min-heap of ladder-assigned climbs lets us swap the smallest ladder climb to bricks when we have too many climbs.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try bricks and ladders for each upward jump, memoizing remaining resources.
```python
class Solution:
    def furthestBuilding(self, heights: list[int], bricks: int, ladders: int) -> int:
        from functools import lru_cache
        n = len(heights)
        @lru_cache(None)
        def dfs(i, b, l):
            if i == n - 1:
                return i
            climb = max(0, heights[i + 1] - heights[i])
            if climb == 0:
                return dfs(i + 1, b, l)
            best = i
            if b >= climb:
                best = max(best, dfs(i + 1, b - climb, l))
            if l > 0:
                best = max(best, dfs(i + 1, b, l - 1))
            return best
        return dfs(0, bricks, ladders)
```
- **Time:** O(n * bricks * ladders) — **Space:** O(n * bricks * ladders)

## Approach 2 — Better
**Idea:** Binary search the answer and check whether the largest climbs in that prefix can be paid by ladders.
```python
class Solution:
    def furthestBuilding(self, heights: list[int], bricks: int, ladders: int) -> int:
        def can(reach):
            climbs = []
            for i in range(reach):
                d = heights[i + 1] - heights[i]
                if d > 0:
                    climbs.append(d)
            climbs.sort(reverse=True)
            return sum(climbs[ladders:]) <= bricks
        lo, hi = 0, len(heights) - 1
        while lo < hi:
            mid = (lo + hi + 1) // 2
            if can(mid):
                lo = mid
            else:
                hi = mid - 1
        return lo
```
- **Time:** O(n log n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Treat each positive climb as ladder-paid, and whenever more than `ladders` climbs are ladder-paid, move the smallest one to bricks.
```python
class Solution:
    def furthestBuilding(self, heights: list[int], bricks: int, ladders: int) -> int:
        import heapq
        ladder_climbs = []
        for i in range(len(heights) - 1):
            climb = heights[i + 1] - heights[i]
            if climb <= 0:
                continue
            heapq.heappush(ladder_climbs, climb)
            if len(ladder_climbs) > ladders:
                bricks -= heapq.heappop(ladder_climbs)
            if bricks < 0:
                return i
        return len(heights) - 1
```
- **Time:** O(n log ladders) — **Space:** O(ladders)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * bricks * ladders) | O(n * bricks * ladders) |
| Better | O(n log n log n) | O(n) |
| Optimal | O(n log ladders) | O(ladders) |

## Edge Cases & Pitfalls
- Downward moves cost nothing.
- If `ladders` is zero, all positive climbs must use bricks.
- Return the current index when bricks first become negative.

## Related
- Minimum Number of Refueling Stops
- Jump Game II
