# 04. Divide Intervals Into Minimum Number of Groups

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Amazon, ByteDance

## Problem
Given inclusive intervals `intervals`, divide them into the minimum number of groups so that no two intervals in the same group intersect. Return that minimum number.

Constraints: `1 <= len(intervals) <= 10^5`, `1 <= start <= end <= 10^6`.

## Examples
```text
Input: intervals = [[5,10],[6,8],[1,5],[2,3],[1,10]]
Output: 3
Explanation: At coordinate 5, intervals [1,5], [5,10], and [1,10] all intersect.
```

## Understanding & Intuition
The minimum number of groups equals the maximum number of intervals overlapping at any point. Because endpoints are inclusive, an interval ending at `x` still conflicts with one starting at `x`. We can compute the peak overlap directly or simulate room assignment.

## Approach 1 — Naive / Brute Force
**Idea:** For every interval endpoint coordinate, count how many intervals contain it.
```python
class Solution:
    def minGroups(self, intervals: list[list[int]]) -> int:
        points = set()
        for start, end in intervals:
            points.add(start)
            points.add(end)
        answer = 0
        for point in points:
            active = 0
            for start, end in intervals:
                if start <= point <= end:
                    active += 1
            answer = max(answer, active)
        return answer
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use difference events; add at `start` and subtract at `end + 1` to model inclusive intervals.
```python
class Solution:
    def minGroups(self, intervals: list[list[int]]) -> int:
        events = []
        for start, end in intervals:
            events.append((start, 1))
            events.append((end + 1, -1))
        events.sort()
        active = 0
        answer = 0
        for _, delta in events:
            active += delta
            answer = max(answer, active)
        return answer
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by start and keep a min-heap of group end times; reuse a group only if its end is strictly before the next start.
```python
class Solution:
    def minGroups(self, intervals: list[list[int]]) -> int:
        import heapq
        intervals.sort()
        heap = []
        answer = 0
        for start, end in intervals:
            while heap and heap[0] < start:
                heapq.heappop(heap)
            heapq.heappush(heap, end)
            answer = max(answer, len(heap))
        return answer
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Endpoints are inclusive, so `[1,2]` and `[2,3]` cannot share a group.
- With sweep events, subtract at `end + 1`, not at `end`.
- Duplicate intervals each require separate groups while overlapping.

## Related
- Meeting Rooms II
- Maximum Number of Events That Can Be Attended
- Car Pooling
