# 01. Kth Largest Element in an Array

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer array `nums` and integer `k`, return the `k`th largest element when values are sorted descending with duplicates kept. It is not the `k`th distinct value.

**Input**
- `nums`: list of integers.
- `k`: one-based descending rank.

**Output**
- The value of the `k`th largest element.

## Constraints
- `1 <= k <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

## Examples
```text
Input: nums = [3,2,1,5,6,4], k = 2
Output: 5
Explanation: In descending order the values are `6, 5, 4, 3, 2, 1`, so the second largest is `5`.
```

## Understanding & Intuition
Sorting fully works but does more than needed. A heap lets us keep only the best `k` candidates seen so far. For the expected optimal interview solution, quickselect partitions around a pivot and only explores the side containing the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Sort all values in descending order and index `k - 1`.
```python
from typing import List

class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        # Full sorting is simple but costs O(n log n).
        nums.sort(reverse=True)
        return nums[k - 1]
```
- **Time:** O(n log n) — **Space:** O(1) extra, depending on sort implementation

## Approach 2 — Better
**Idea:** Maintain a min-heap of size `k`; its root is the kth largest after all numbers are processed.
```python
from typing import List
import heapq

class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        heap = []
        for num in nums:
            heapq.heappush(heap, num)
            # Remove smaller elements so only k largest remain.
            if len(heap) > k:
                heapq.heappop(heap)
        return heap[0]
```
- **Time:** O(n log k) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Use iterative quickselect to place the target index `len(nums) - k` as it would appear in ascending order.
```python
from typing import List
import random

class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        target = len(nums) - k
        left, right = 0, len(nums) - 1

        while left <= right:
            pivot_index = random.randint(left, right)
            pivot = nums[pivot_index]
            nums[pivot_index], nums[right] = nums[right], nums[pivot_index]
            store = left

            # Move values smaller than pivot to the left.
            for i in range(left, right):
                if nums[i] < pivot:
                    nums[store], nums[i] = nums[i], nums[store]
                    store += 1

            nums[store], nums[right] = nums[right], nums[store]

            if store == target:
                return nums[store]
            if store < target:
                left = store + 1
            else:
                right = store - 1

        return -1
```
- **Time:** O(n) average, O(n^2) worst — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(1) extra |
| Better | O(n log k) | O(k) |
| Optimal | O(n) average | O(1) |

## Edge Cases & Pitfalls
- Duplicates count as separate elements.
- `k = 1` returns the maximum; `k = len(nums)` returns the minimum.
- Quickselect mutates the input array.

## Related
- Top K Frequent Elements
- K Closest Points to Origin
- Wiggle Sort II
