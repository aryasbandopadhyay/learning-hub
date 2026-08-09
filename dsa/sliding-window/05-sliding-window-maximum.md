# 05. Sliding Window Maximum

- **Difficulty:** Hard
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer array `nums` and a window size `k`, slide the window from left to right and record the maximum value in each window.

**Input**
- `nums`: a list of integers.
- `k`: the fixed window length.

**Output**
- A list of window maximums in left-to-right window order. **This judge compares exactly** to that order.

## Constraints
- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`
- `1 <= k <= nums.length`

## Examples
```text
Input: nums = [1,3,-1,-3,5,3,6,7], k = 3
Output: [3,3,5,5,6,7]
Explanation: The first window `[1,3,-1]` has maximum `3`; moving one step at a time gives maxima `3,3,5,5,6,7`.
```

## Understanding & Intuition
The maximum of adjacent windows changes slowly: one value leaves and one enters. A data structure can preserve candidates instead of scanning the whole window each time. A monotonic deque keeps only values that can still become maximum.

## Approach 1 — Naive / Brute Force
**Idea:** Compute `max` separately for each window.
```python
from typing import List

class Solution:
    def maxSlidingWindow(self, nums: List[int], k: int) -> List[int]:
        ans = []
        for left in range(len(nums) - k + 1):
            ans.append(max(nums[left:left + k]))
        return ans
```
- **Time:** O(nk) — **Space:** O(1) excluding output

## Approach 2 — Better
**Idea:** Use a max-heap and lazily discard values whose indexes left the window.
```python
from typing import List
import heapq

class Solution:
    def maxSlidingWindow(self, nums: List[int], k: int) -> List[int]:
        heap = []
        ans = []
        for i, value in enumerate(nums):
            heapq.heappush(heap, (-value, i))
            # Remove heap top entries that are outside the current window.
            while heap[0][1] <= i - k:
                heapq.heappop(heap)
            if i >= k - 1:
                ans.append(-heap[0][0])
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep indexes in decreasing value order; the front is the current maximum.
```python
from typing import List
from collections import deque

class Solution:
    def maxSlidingWindow(self, nums: List[int], k: int) -> List[int]:
        dq = deque()
        ans = []
        for i, value in enumerate(nums):
            if dq and dq[0] <= i - k:
                dq.popleft()
            # Smaller values behind the new value can never be maximum again.
            while dq and nums[dq[-1]] <= value:
                dq.pop()
            dq.append(i)
            if i >= k - 1:
                ans.append(nums[dq[0]])
        return ans
```
- **Time:** O(n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nk) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(k) |

## Edge Cases & Pitfalls
- `k = 1` returns the original array.
- Store indexes, not just values, to know when elements expire.
- Pop from the deque back while the new value is greater or equal.

## Related
- Minimum Size Subarray Sum
- Maximum Average Subarray I

