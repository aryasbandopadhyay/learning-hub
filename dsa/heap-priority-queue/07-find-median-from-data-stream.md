# 07. Find Median from Data Stream

- **Difficulty:** Hard
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft, Apple

## Problem
Design a data structure that supports `addNum(num)` and `findMedian()`, returning the median of all numbers added so far. If the count is even, return the average of the two middle values. Constraints: up to `5 * 10^4` operations, `-10^5 <= num <= 10^5`.

## Examples
```text
Input: ["MedianFinder","addNum","addNum","findMedian","addNum","findMedian"], [[],[1],[2],[],[3],[]]
Output: [null,null,null,1.5,null,2.0]
Explanation: Median of [1,2] is 1.5; median of [1,2,3] is 2.
```

## Understanding & Intuition
The median depends on the middle of the sorted order. Re-sorting on every query is too slow for many operations. Two heaps split the lower and upper halves so the middle value is always at a heap root.

## Approach 1 — Naive / Brute Force
**Idea:** Store all numbers and sort them every time `findMedian` is called.
```python
class MedianFinder:
    def __init__(self):
        self.nums = []

    def addNum(self, num: int) -> None:
        self.nums.append(num)

    def findMedian(self) -> float:
        ordered = sorted(self.nums)
        n = len(ordered)
        mid = n // 2
        if n % 2 == 1:
            return float(ordered[mid])
        return (ordered[mid - 1] + ordered[mid]) / 2
```
- **Time:** O(n log n) per median — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep the array sorted using binary insertion, making median lookup O(1).
```python
import bisect

class MedianFinder:
    def __init__(self):
        self.nums = []

    def addNum(self, num: int) -> None:
        # Search is O(log n), but shifting elements is O(n).
        bisect.insort(self.nums, num)

    def findMedian(self) -> float:
        n = len(self.nums)
        mid = n // 2
        if n % 2 == 1:
            return float(self.nums[mid])
        return (self.nums[mid - 1] + self.nums[mid]) / 2
```
- **Time:** O(n) per add, O(1) per median — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a max-heap for the lower half and a min-heap for the upper half, rebalancing sizes after each insert.
```python
import heapq

class MedianFinder:
    def __init__(self):
        self.small = []  # Max-heap via negative numbers.
        self.large = []  # Min-heap.

    def addNum(self, num: int) -> None:
        heapq.heappush(self.small, -num)

        # Ensure every value in small is <= every value in large.
        if self.large and -self.small[0] > self.large[0]:
            heapq.heappush(self.large, -heapq.heappop(self.small))

        # Keep size difference at most one, favoring small.
        if len(self.small) > len(self.large) + 1:
            heapq.heappush(self.large, -heapq.heappop(self.small))
        elif len(self.large) > len(self.small):
            heapq.heappush(self.small, -heapq.heappop(self.large))

    def findMedian(self) -> float:
        if len(self.small) > len(self.large):
            return float(-self.small[0])
        return (-self.small[0] + self.large[0]) / 2
```
- **Time:** O(log n) per add, O(1) per median — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) per median | O(n) |
| Better | O(n) per add | O(n) |
| Optimal | O(log n) per add | O(n) |

## Edge Cases & Pitfalls
- Rebalance after every insertion.
- Use float division for even counts.
- Negative values work naturally with the max-heap negation trick.

## Related
- Sliding Window Median
- Kth Largest Element in a Stream
- Data Stream as Disjoint Intervals
