# 02. Kth Largest Element in a Stream

- **Difficulty:** Easy
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Design `KthLargest`. The constructor receives `k` and initial values `nums`. Each `add(val)` inserts a value into the stream and returns the current `k`th largest among all values seen so far.

**Input**
- A sequence of `KthLargest(k, nums)` and `add(val)` operations.

**Output**
- Results in order: constructor returns `null`, each `add` returns the current `k`th largest value. This judge compares exactly one result per operation.

## Constraints
- `1 <= k <= 10^4`
- `0 <= nums.length <= 10^4`
- `-10^4 <= nums[i], val <= 10^4`
- At most `10^4` calls are made to `add`.
- There are at least `k` stream values whenever an `add` result is requested.

## Examples
```text
Input: ["KthLargest","add","add","add","add","add"], [[3,[4,5,8,2]],[3],[5],[10],[9],[4]]
Output: [null,4,5,5,8,8]
Explanation: After each insertion, the third largest becomes `4`, then `5`, stays `5`, becomes `8`, and stays `8`.
```

## Understanding & Intuition
The stream grows one element at a time, so repeated full sorting is wasteful. We only need the top `k` values at any moment. A size-`k` min-heap stores those values, making the smallest heap item the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Store every number and sort after each `add`.
```python
from typing import List

class KthLargest:
    def __init__(self, k: int, nums: List[int]):
        self.k = k
        self.nums = nums[:]

    def add(self, val: int) -> int:
        self.nums.append(val)
        # Re-sort every time; straightforward but expensive.
        ordered = sorted(self.nums, reverse=True)
        return ordered[self.k - 1]
```
- **Time:** O(n log n) per add — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep the stream sorted with binary insertion, then read index `len(nums) - k`.
```python
from typing import List
import bisect

class KthLargest:
    def __init__(self, k: int, nums: List[int]):
        self.k = k
        self.nums = sorted(nums)

    def add(self, val: int) -> int:
        # Insertion search is logarithmic, shifting is linear.
        bisect.insort(self.nums, val)
        return self.nums[len(self.nums) - self.k]
```
- **Time:** O(n) per add — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain a min-heap of only the current `k` largest values.
```python
from typing import List
import heapq

class KthLargest:
    def __init__(self, k: int, nums: List[int]):
        self.k = k
        self.heap = nums[:]
        heapq.heapify(self.heap)
        # Trim everything below the kth largest.
        while len(self.heap) > k:
            heapq.heappop(self.heap)

    def add(self, val: int) -> int:
        heapq.heappush(self.heap, val)
        if len(self.heap) > self.k:
            heapq.heappop(self.heap)
        return self.heap[0]
```
- **Time:** O(log k) per add — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) per add | O(n) |
| Better | O(n) per add | O(n) |
| Optimal | O(log k) per add | O(k) |

## Edge Cases & Pitfalls
- Initial `nums` may contain fewer than `k` elements, but LeetCode guarantees `add` is called when an answer exists.
- Do not use a max-heap of all elements; it cannot reveal kth largest without popping.
- Keep duplicates because stream elements are counted individually.

## Related
- Kth Largest Element in an Array
- Find Median from Data Stream
- Moving Average from Data Stream
