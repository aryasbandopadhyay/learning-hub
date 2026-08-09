# 02. Two Sum II - Input Array Is Sorted

- **Difficulty:** Medium
- **Pattern:** Two Pointers
- **Asked at:** Amazon, Bloomberg, Apple, Microsoft

## Problem
Given a non-decreasing array `numbers` and a target, find two different entries whose sum is `target`. The input has exactly one solution.

**Input**
- `numbers`: a 1-indexed, non-decreasing list of integers.
- `target`: the sum to form.

**Output**
- A list `[index1, index2]` using **1-based** indices with `index1 < index2`. **This judge compares exactly** to that order.

## Constraints
- `2 <= numbers.length <= 3 * 10^4`
- `-1000 <= numbers[i] <= 1000`
- `numbers` is sorted in non-decreasing order.
- Exactly one valid pair exists.

## Examples
```text
Input: numbers = [2,7,11,15], target = 9
Output: [1,2]
Explanation: `numbers[1] + numbers[2] = 2 + 7 = 9` using 1-based indexing, so return `[1,2]`.
```

## Understanding & Intuition
The array is sorted, so sums become larger when the left pointer moves right and smaller when the right pointer moves left. That monotonic behavior lets us discard many impossible pairs.

## Approach 1 — Naive / Brute Force
**Idea:** Try every pair until the required sum is found.
```python
from typing import List

class Solution:
    def twoSum(self, numbers: List[int], target: int) -> List[int]:
        n = len(numbers)
        for i in range(n):
            for j in range(i + 1, n):
                # Return 1-indexed positions as required.
                if numbers[i] + numbers[j] == target:
                    return [i + 1, j + 1]
        return []
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** For each value, binary search for its complement to the right.
```python
from typing import List
import bisect

class Solution:
    def twoSum(self, numbers: List[int], target: int) -> List[int]:
        for i, value in enumerate(numbers):
            need = target - value
            j = bisect.bisect_left(numbers, need, i + 1)
            # Check that binary search found the exact complement.
            if j < len(numbers) and numbers[j] == need:
                return [i + 1, j + 1]
        return []
```
- **Time:** O(n log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use one pointer at each end and move the pointer that can improve the sum.
```python
from typing import List

class Solution:
    def twoSum(self, numbers: List[int], target: int) -> List[int]:
        left, right = 0, len(numbers) - 1
        while left < right:
            total = numbers[left] + numbers[right]
            if total == target:
                return [left + 1, right + 1]
            if total < target:
                left += 1
            else:
                right -= 1
        return []
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n log n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Return 1-indexed, not 0-indexed, positions.
- Do not reuse the same element.
- Duplicates are valid and may be part of the answer.

## Related
- Two Sum
- 3Sum
- 4Sum
