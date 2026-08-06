# 15. Find K Closest Elements

- **Difficulty:** Medium
- **Pattern:** Binary Search / Heap
- **Asked at:** Meta, Google, Amazon

## Problem
You are given a sorted integer array `arr`, an integer `k`, and an integer `x`. Return the `k` integers closest to `x`, sorted in ascending order. If two numbers are equally close, the smaller number is considered closer.

Constraints: `1 <= k <= len(arr) <= 10^4`, `arr` is sorted in ascending order, and `-10^4 <= arr[i], x <= 10^4`.

## Examples
```text
Input: arr = [1,2,3,4,5], k = 4, x = 3
Output: [1,2,3,4]
Explanation: The four closest values to 3 are 1, 2, 3, and 4.
```

## Understanding & Intuition
The answer is always a contiguous window in the sorted array. Ties prefer smaller values, so when the left and right candidates are equally distant, the left side wins. We can either sort by closeness or directly find the best window.

## Approach 1 — Naive / Brute Force
**Idea:** Sort all values by `(distance from x, value)`, take the first `k`, then sort the selected values for the required output order.
```python
class Solution:
    def findClosestElements(self, arr, k, x):
        chosen = sorted(arr, key=lambda value: (abs(value - x), value))[:k]
        return sorted(chosen)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Binary search for the insertion point of `x`, then expand two pointers outward while applying the tie rule.
```python
class Solution:
    def findClosestElements(self, arr, k, x):
        from bisect import bisect_left
        right = bisect_left(arr, x)
        left = right - 1
        result = []
        while len(result) < k:
            if left < 0:
                result.append(arr[right])
                right += 1
            elif right >= len(arr):
                result.append(arr[left])
                left -= 1
            elif abs(arr[left] - x) <= abs(arr[right] - x):
                result.append(arr[left])
                left -= 1
            else:
                result.append(arr[right])
                right += 1
        return sorted(result)
```
- **Time:** O(log n + k log k) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Binary search the left boundary of the length-`k` window. Compare whether `x` is closer to `arr[mid]` or `arr[mid + k]` to decide which side can be discarded.
```python
class Solution:
    def findClosestElements(self, arr, k, x):
        low, high = 0, len(arr) - k
        while low < high:
            mid = (low + high) // 2
            if x - arr[mid] > arr[mid + k] - x:
                low = mid + 1
            else:
                high = mid
        return arr[low:low + k]
```
- **Time:** O(log(n - k) + k) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(log n + k log k) | O(k) |
| Optimal | O(log(n - k) + k) | O(k) |

## Edge Cases & Pitfalls
- The result must be sorted ascending, not by closeness.
- Equal distances choose the smaller value.
- When `k == len(arr)`, the whole array is the answer.

## Related
- Binary Search
- K Closest Points to Origin
- Find First and Last Position of Element in Sorted Array
