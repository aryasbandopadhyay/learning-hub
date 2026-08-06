# 07. Running Median of a Stream

- **Difficulty:** Hard
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given a fixed stream represented by `nums`, return a list where the `i`th value is the median after reading `nums[0:i+1]`. For an even count, the median is the average of the two middle values. Constraints: `1 <= len(nums) <= 100000`.

## Examples
```text
Input: nums = [5,15,1,3]
Output: [5.0,10.0,5.0,4.0]
Explanation: The sorted prefixes are [5], [5,15], [1,5,15], and [1,3,5,15].
```

## Understanding & Intuition
The median depends on the boundary between the lower and upper halves of seen numbers. Maintaining sorted prefixes works but is slow. Two heaps keep the lower half and upper half balanced around that boundary.

## Approach 1 — Naive / Brute Force
**Idea:** Sort every prefix independently and compute its median.
```python
class Solution:
    def runningMedian(self, nums: list[int]) -> list[float]:
        ans = []
        for i in range(len(nums)):
            cur = sorted(nums[:i + 1])
            mid = i // 2
            if (i + 1) % 2:
                ans.append(float(cur[mid]))
            else:
                ans.append((cur[mid] + cur[mid + 1]) / 2.0)
        return ans
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Insert each number into a sorted list using binary search, then read the middle.
```python
class Solution:
    def runningMedian(self, nums: list[int]) -> list[float]:
        import bisect
        arr = []
        ans = []
        for x in nums:
            bisect.insort(arr, x)
            n = len(arr)
            if n % 2:
                ans.append(float(arr[n // 2]))
            else:
                ans.append((arr[n // 2 - 1] + arr[n // 2]) / 2.0)
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a max-heap for the lower half and a min-heap for the upper half, rebalancing after each insertion.
```python
class Solution:
    def runningMedian(self, nums: list[int]) -> list[float]:
        import heapq
        low = []
        high = []
        ans = []
        for x in nums:
            if not low or x <= -low[0]:
                heapq.heappush(low, -x)
            else:
                heapq.heappush(high, x)
            if len(low) > len(high) + 1:
                heapq.heappush(high, -heapq.heappop(low))
            if len(high) > len(low):
                heapq.heappush(low, -heapq.heappop(high))
            if len(low) == len(high):
                ans.append((-low[0] + high[0]) / 2.0)
            else:
                ans.append(float(-low[0]))
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Even prefixes need an average, not one of the two middle values.
- Return floats consistently, even for integer medians.
- Rebalance heaps after every insertion.

## Related
- Sliding Window Median
- Kth Largest Element in a Stream
