# 13. Minimize Deviation in Array

- **Difficulty:** Hard
- **Pattern:** Heap / Greedy
- **Asked at:** Amazon, Google, Microsoft

## Problem
You are given an array `nums`. In one operation, you may divide an even number by `2` or multiply an odd number by `2`. The deviation is the difference between the maximum and minimum array values. Return the minimum possible deviation after any number of operations.

**Input**
- `nums`: a `list[int]`; the input integer list.

**Output**
- A `int`. Return the minimum possible deviation after any number of operations.

## Constraints
- `2 <= len(nums) <= 10^5`, `1 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [1,2,3,4]
Output: 1
Explanation: Transform the array to [2,2,3,2], whose deviation is 1.
```

## Understanding & Intuition
Each number has a finite set of useful values: make an odd number even once, then repeatedly halve while possible. The task is to choose one reachable value per original number to minimize the range. The optimal greedy view starts from each number's largest useful value and repeatedly reduces the current maximum.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every reachable value for each number, merge all candidates, and use a sliding window that contains at least one candidate from every original position.
```python
class Solution:
    def minimumDeviation(self, nums):
        candidates = []
        for idx, num in enumerate(nums):
            value = num * 2 if num % 2 else num
            seen = set()
            while True:
                seen.add(value)
                if value % 2:
                    break
                value //= 2
            for value in seen:
                candidates.append((value, idx))
        candidates.sort()
        need = len(nums)
        counts = [0] * need
        covered = 0
        left = 0
        best = candidates[-1][0] - candidates[0][0]
        for right, (value, idx) in enumerate(candidates):
            if counts[idx] == 0:
                covered += 1
            counts[idx] += 1
            while covered == need:
                best = min(best, value - candidates[left][0])
                left_idx = candidates[left][1]
                counts[left_idx] -= 1
                if counts[left_idx] == 0:
                    covered -= 1
                left += 1
        return best
```
- **Time:** O(m log m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Generate each number's sorted reachable list and solve the smallest-range-over-k-lists problem with a min-heap while tracking the current maximum.
```python
class Solution:
    def minimumDeviation(self, nums):
        import heapq
        lists = []
        current_max = 0
        for num in nums:
            value = num * 2 if num % 2 else num
            values = []
            while True:
                values.append(value)
                if value % 2:
                    break
                value //= 2
            values.sort()
            lists.append(values)
            current_max = max(current_max, values[0])
        heap = []
        for i, values in enumerate(lists):
            heapq.heappush(heap, (values[0], i, 0))
        best = float("inf")
        while len(heap) == len(lists):
            value, list_idx, pos = heapq.heappop(heap)
            best = min(best, current_max - value)
            pos += 1
            if pos == len(lists[list_idx]):
                break
            next_value = lists[list_idx][pos]
            current_max = max(current_max, next_value)
            heapq.heappush(heap, (next_value, list_idx, pos))
        return best
```
- **Time:** O(m log n) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Normalize every number to its largest useful value by doubling odds. Track the minimum value and use a max-heap; while the maximum is even, halve it and update the answer.
```python
class Solution:
    def minimumDeviation(self, nums):
        import heapq
        heap = []
        current_min = float("inf")
        for num in nums:
            value = num * 2 if num % 2 else num
            current_min = min(current_min, value)
            heapq.heappush(heap, -value)
        best = float("inf")
        while heap:
            current_max = -heapq.heappop(heap)
            best = min(best, current_max - current_min)
            if current_max % 2:
                break
            reduced = current_max // 2
            current_min = min(current_min, reduced)
            heapq.heappush(heap, -reduced)
        return best
```
- **Time:** O(n log n log M) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m log m) | O(m) |
| Better | O(m log n) | O(m) |
| Optimal | O(n log n log M) | O(n) |

## Edge Cases & Pitfalls
- Odd numbers should be doubled at most once before reductions begin.
- Update the answer before deciding whether the current maximum can be reduced.
- `m` is the total number of reachable values, at most O(n log M).

## Related
- Smallest Range Covering Elements from K Lists
- Last Stone Weight
- Kth Largest Element in an Array
