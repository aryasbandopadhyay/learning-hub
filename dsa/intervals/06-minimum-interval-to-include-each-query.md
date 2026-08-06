# 06. Minimum Interval to Include Each Query

- **Difficulty:** Hard
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Meta, Apple

## Problem
Given intervals and queries, for each query return the size of the smallest interval `[left, right]` containing it, or `-1` if none exists. Interval size is `right - left + 1`. Constraints: `1 <= len(intervals), len(queries) <= 10^5`.

## Examples
```text
Input: intervals = [[1,4],[2,4],[3,6],[4,4]], queries = [2,3,4,5]
Output: [3,3,1,4]
Explanation: Query 4 is contained by [4,4], whose size is 1.
```

## Understanding & Intuition
A query can only use intervals whose left endpoint is no greater than the query. Among those, expired intervals with right endpoint less than the query are invalid. A min-heap by interval length keeps the best currently valid interval on top.

## Approach 1 — Naive / Brute Force
**Idea:** For every query, scan every interval and keep the smallest containing interval.
```python
from typing import List

class Solution:
    def minInterval(self, intervals: List[List[int]], queries: List[int]) -> List[int]:
        answer = []
        for q in queries:
            best = float("inf")
            for left, right in intervals:
                if left <= q <= right:
                    best = min(best, right - left + 1)
            answer.append(-1 if best == float("inf") else best)
        return answer
```
- **Time:** O(nq) — **Space:** O(1) besides output

## Approach 2 — Better
**Idea:** Sort intervals, then binary-search candidate starts for each query and scan only intervals with `left <= query`.
```python
from typing import List
import bisect

class Solution:
    def minInterval(self, intervals: List[List[int]], queries: List[int]) -> List[int]:
        intervals.sort(key=lambda x: x[0])
        starts = [left for left, _ in intervals]
        result = []

        for q in queries:
            limit = bisect.bisect_right(starts, q)
            best = float("inf")
            for i in range(limit):
                left, right = intervals[i]
                if right >= q:
                    best = min(best, right - left + 1)
            result.append(-1 if best == float("inf") else best)
        return result
```
- **Time:** O((n + q) log n + nq) worst case — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Process queries in sorted order; add newly eligible intervals to a min-heap and remove expired intervals.
```python
from typing import List
import heapq

class Solution:
    def minInterval(self, intervals: List[List[int]], queries: List[int]) -> List[int]:
        intervals.sort(key=lambda x: x[0])
        indexed_queries = sorted((q, i) for i, q in enumerate(queries))
        answer = [-1] * len(queries)
        heap = []  # (interval_size, right)
        i = 0

        for q, original_index in indexed_queries:
            while i < len(intervals) and intervals[i][0] <= q:
                left, right = intervals[i]
                heapq.heappush(heap, (right - left + 1, right))
                i += 1
            while heap and heap[0][1] < q:
                heapq.heappop(heap)
            if heap:
                answer[original_index] = heap[0][0]
        return answer
```
- **Time:** O((n + q) log n) — **Space:** O(n + q)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nq) | O(1) |
| Better | O((n + q) log n + nq) worst case | O(n) |
| Optimal | O((n + q) log n) | O(n + q) |

## Edge Cases & Pitfalls
- Preserve original query order after sorting queries.
- Remove heap entries whose right endpoint is less than the current query.
- Interval length is inclusive: `right - left + 1`.

## Related
- Meeting Rooms II
- Insert Interval
