# 01. The Skyline Problem

- **Difficulty:** Hard
- **Pattern:** interval merging & sweep line
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given a list of buildings where each building is `[left, right, height]`, return the skyline formed by these buildings as a list of key points `[x, height]`. The skyline height changes only at key points, adjacent points must not have the same height, and the final point must have height `0`.

**Input**
- `buildings`: a `list[list[int]]`; building triples `[left, right, height]`.

**Output**
- A `list[list[int]]`. Return the skyline formed by these buildings as a list of key points `[x, height]`. This judge compares the sequence exactly: return key points sorted by increasing `x`; do not include consecutive points with the same height.

## Constraints
- `0 <= len(buildings) <= 2000`, `0 <= left < right <= 10^9`, and `1 <= height <= 10^9`.

## Examples
```text
Input: buildings = [[2,9,10],[3,7,15],[5,12,12],[15,20,10],[19,24,8]]
Output: [[2,10],[3,15],[7,12],[12,0],[15,10],[20,8],[24,0]]
Explanation: The maximum active building height changes at each returned x-coordinate. The output is written in the required deterministic order.
```

## Understanding & Intuition
The visible skyline is the upper envelope of all building intervals. Every change can only happen at a building start or end, so sweep-line methods focus on those critical coordinates. The hard part is keeping the current maximum height while buildings expire.

## Approach 1 — Naive / Brute Force
**Idea:** Check every critical x-coordinate and scan all buildings to find the height immediately after that coordinate.
```python
class Solution:
    def getSkyline(self, buildings: list[list[int]]) -> list[list[int]]:
        xs = sorted(set([x for l, r, h in buildings for x in (l, r)]))
        ans = []
        for x in xs:
            height = 0
            for l, r, h in buildings:
                if l <= x < r and h > height:
                    height = h
            if not ans or ans[-1][1] != height:
                ans.append([x, height])
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sweep sorted events and maintain active heights in a sorted list using `bisect`.
```python
class Solution:
    def getSkyline(self, buildings: list[list[int]]) -> list[list[int]]:
        from bisect import insort, bisect_left
        events = []
        for l, r, h in buildings:
            events.append((l, -h))
            events.append((r, h))
        events.sort()
        active = [0]
        ans = []
        i = 0
        while i < len(events):
            x = events[i][0]
            while i < len(events) and events[i][0] == x:
                h = events[i][1]
                if h < 0:
                    insort(active, -h)
                else:
                    j = bisect_left(active, h)
                    active.pop(j)
                i += 1
            cur = active[-1]
            if not ans or ans[-1][1] != cur:
                ans.append([x, cur])
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Add start events to a max-heap and lazily remove buildings whose right edge is no longer active.
```python
class Solution:
    def getSkyline(self, buildings: list[list[int]]) -> list[list[int]]:
        import heapq
        events = []
        for l, r, h in buildings:
            events.append((l, -h, r))
            events.append((r, 0, 0))
        events.sort()
        heap = [(0, float('inf'))]
        ans = []
        i = 0
        while i < len(events):
            x = events[i][0]
            while i < len(events) and events[i][0] == x:
                neg_h, r = events[i][1], events[i][2]
                if neg_h:
                    heapq.heappush(heap, (neg_h, r))
                i += 1
            while heap and heap[0][1] <= x:
                heapq.heappop(heap)
            cur = -heap[0][0]
            if not ans or ans[-1][1] != cur:
                ans.append([x, cur])
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Process all events at the same x before emitting a point.
- Right edges are exclusive for active buildings.
- Suppress consecutive points with equal heights.

## Related
- Meeting Rooms II
- Describe the Painting
