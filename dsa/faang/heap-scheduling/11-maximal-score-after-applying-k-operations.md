# 11. Maximal Score After Applying K Operations

- **Difficulty:** Medium
- **Pattern:** Heap / Greedy
- **Asked at:** Amazon, Google, Microsoft

## Problem
You are given an integer array `nums` and an integer `k`. In one operation, choose the largest available number `x`, add `x` to your score, remove it, and insert `ceil(x / 3)` back into the array. Return the maximum score after exactly `k` operations.

Constraints: `1 <= len(nums) <= 10^5`, `1 <= nums[i] <= 10^9`, `1 <= k <= 10^5`.

## Examples
```text
Input: nums = [10,10,10,10,10], k = 5
Output: 50
Explanation: Pick 10 in all five operations for a total score of 50.
```

## Understanding & Intuition
The greedy choice is forced: taking a smaller element while a larger one exists can never improve the current or future score. After choosing the maximum, only that value changes. A max-priority structure is therefore the natural fit.

## Approach 1 — Naive / Brute Force
**Idea:** Sort the array before every operation, take the last value, replace it with `ceil(value / 3)`, and repeat.
```python
class Solution:
    def maxKelements(self, nums, k):
        arr = nums[:]
        score = 0
        for _ in range(k):
            arr.sort()
            value = arr.pop()
            score += value
            arr.append((value + 2) // 3)
        return score
```
- **Time:** O(k n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep a sorted list of negative values. Removing the first value gives the maximum original value, and binary insertion keeps the list ordered after replacement.
```python
class Solution:
    def maxKelements(self, nums, k):
        from bisect import insort
        arr = sorted(-x for x in nums)
        score = 0
        for _ in range(k):
            value = -arr.pop(0)
            score += value
            insort(arr, -((value + 2) // 3))
        return score
```
- **Time:** O(n log n + k n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use `heapq` as a max-heap by storing negative values. Each operation is one pop and one push.
```python
class Solution:
    def maxKelements(self, nums, k):
        import heapq
        heap = [-x for x in nums]
        heapq.heapify(heap)
        score = 0
        for _ in range(k):
            value = -heapq.heappop(heap)
            score += value
            heapq.heappush(heap, -((value + 2) // 3))
        return score
```
- **Time:** O((n + k) log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k n log n) | O(n) |
| Better | O(n log n + k n) | O(n) |
| Optimal | O((n + k) log n) | O(n) |

## Edge Cases & Pitfalls
- Use `(value + 2) // 3` for integer `ceil(value / 3)`.
- Do not mutate the caller's `nums` list.
- The score can be large, so return a Python integer.

## Related
- Kth Largest Element in an Array
- Last Stone Weight
- Maximum Average Pass Ratio
