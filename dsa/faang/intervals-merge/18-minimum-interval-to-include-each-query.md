# 18. Minimum Interval to Include Each Query

- **Difficulty:** Hard
- **Pattern:** intervals & heap
- **Asked at:** Google, Amazon, Meta

## Problem
For each value in `queries`, find the length of the smallest interval `[start, end]` from `intervals` such that `start <= query <= end`. The length of an interval is `end - start + 1`. Return `-1` for a query not contained in any interval, preserving the original query order.

Constraints: `1 <= len(intervals), len(queries) <= 10^5`, `1 <= start <= end <= 10^7`, and `1 <= queries[i] <= 10^7`.

## Examples
```text
Input: intervals = [[1,4],[2,4],[3,6],[4,4]], queries = [2,3,4,5]
Output: [3,3,1,4]
Explanation: The best interval lengths for queries 2, 3, 4, and 5 are 3, 3, 1, and 4.
```

## Understanding & Intuition
Each query only cares about intervals that have started and not yet ended at that query value. Sorting queries allows intervals to be added as they become eligible. A min-heap ordered by interval length then exposes the smallest currently valid interval.

## Approach 1 — Naive / Brute Force
**Idea:** For every query, scan all intervals and keep the minimum containing length.
```python
class Solution:
    def minInterval(self, intervals: list[list[int]], queries: list[int]) -> list[int]:
        ans = []
        for q in queries:
            best = None
            for start, end in intervals:
                if start <= q <= end:
                    length = end - start + 1
                    if best is None or length < best:
                        best = length
            ans.append(-1 if best is None else best)
        return ans
```
- **Time:** O(nq) — **Space:** O(1) besides output

## Approach 2 — Better
**Idea:** Sort intervals by length and, for each query, take the first interval that contains it.
```python
class Solution:
    def minInterval(self, intervals: list[list[int]], queries: list[int]) -> list[int]:
        by_length = sorted(intervals, key=lambda x: (x[1] - x[0] + 1, x[0], x[1]))
        ans = []
        for q in queries:
            found = -1
            for start, end in by_length:
                if start <= q <= end:
                    found = end - start + 1
                    break
            ans.append(found)
        return ans
```
- **Time:** O(n log n + nq) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Process queries in sorted order, pushing started intervals into a min-heap and popping expired intervals.
```python
class Solution:
    def minInterval(self, intervals: list[list[int]], queries: list[int]) -> list[int]:
        import heapq
        intervals = sorted(intervals)
        indexed_queries = sorted((q, i) for i, q in enumerate(queries))
        ans = [-1] * len(queries)
        heap = []
        i = 0
        for q, idx in indexed_queries:
            while i < len(intervals) and intervals[i][0] <= q:
                start, end = intervals[i]
                heapq.heappush(heap, (end - start + 1, end))
                i += 1
            while heap and heap[0][1] < q:
                heapq.heappop(heap)
            if heap:
                ans[idx] = heap[0][0]
        return ans
```
- **Time:** O((n + q) log n) — **Space:** O(n + q)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nq) | O(1) |
| Better | O(n log n + nq) | O(n) |
| Optimal | O((n + q) log n) | O(n + q) |

## Edge Cases & Pitfalls
- Interval length is inclusive: `end - start + 1`.
- Preserve the original order of `queries`.
- Remove heap intervals whose end is less than the current query.

## Related
- Number of Flowers in Full Bloom
- Meeting Rooms II
