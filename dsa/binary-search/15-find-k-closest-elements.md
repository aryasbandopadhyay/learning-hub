# 15. Find K Closest Elements

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Google, Facebook, Amazon, LinkedIn

## Problem
Given a sorted integer array `arr`, integers `k` and `x`, return the `k` closest integers to `x` in ascending order. A smaller value wins ties. Constraints: `1 <= k <= arr.length <= 10^4`, `-10^4 <= arr[i], x <= 10^4`.

## Examples
```text
Input: arr = [1,2,3,4,5], k = 4, x = 3
Output: [1,2,3,4]
Explanation: The four closest values to 3 are 1, 2, 3, and 4.
```

## Understanding & Intuition
The answer is always a contiguous window of length `k` in the sorted array. We can binary search the left boundary of that window. If `x - arr[mid] > arr[mid + k] - x`, the better window starts to the right; otherwise it starts at `mid` or left.

## Approach 1 — Naive / Brute Force
**Idea:** Sort by distance, take `k`, then sort the answer.
```python
from typing import List

class Solution:
    def findClosestElements(self, arr: List[int], k: int, x: int) -> List[int]:
        chosen = sorted(arr, key=lambda value: (abs(value - x), value))[:k]
        return sorted(chosen)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use two pointers to shrink the window until length `k`.
```python
from typing import List

class Solution:
    def findClosestElements(self, arr: List[int], k: int, x: int) -> List[int]:
        left, right = 0, len(arr) - 1
        while right - left + 1 > k:
            if abs(arr[left] - x) <= abs(arr[right] - x):
                right -= 1
            else:
                left += 1
        return arr[left:right + 1]
```
- **Time:** O(n - k) — **Space:** O(1) excluding output

## Approach 3 — Optimal
**Idea:** Binary search the best left boundary among all length-`k` windows.
```python
from typing import List

class Solution:
    def findClosestElements(self, arr: List[int], k: int, x: int) -> List[int]:
        left, right = 0, len(arr) - k
        while left < right:
            mid = (left + right) // 2
            if x - arr[mid] > arr[mid + k] - x:
                left = mid + 1
            else:
                right = mid
        return arr[left:left + k]
```
- **Time:** O(log(n-k) + k) — **Space:** O(1) excluding output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n - k) | O(1) |
| Optimal | O(log(n-k) + k) | O(1) |

## Edge Cases & Pitfalls
- Ties prefer smaller values, handled by moving or keeping the left window.
- If `x` is outside the array range, the answer is one end window.
- The returned list must be ascending.

## Related
- Binary Search
- K Closest Points to Origin

