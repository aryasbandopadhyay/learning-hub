# 14. Single Element in a Sorted Array

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Amazon, Google, Microsoft, Adobe

## Problem
Given a sorted array where every element appears exactly twice except one element that appears once, return the single element. Constraints: `1 <= nums.length <= 10^5`, `0 <= nums[i] <= 10^5`, and the solution must run in `O(log n)` time and `O(1)` space.

## Examples
```text
Input: nums = [1,1,2,3,3,4,4,8,8]
Output: 2
Explanation: 2 is the only value that appears once.
```

## Understanding & Intuition
Before the single element, pairs start at even indices. After it, the pattern shifts and pairs start at odd indices. Binary search can detect which side still follows the even-pair pattern.

## Approach 1 — Naive / Brute Force
**Idea:** Count occurrences with a dictionary.
```python
from typing import List

class Solution:
    def singleNonDuplicate(self, nums: List[int]) -> int:
        counts = {}
        for num in nums:
            counts[num] = counts.get(num, 0) + 1
        for num, count in counts.items():
            if count == 1:
                return num
        return -1
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** XOR all values; duplicate pairs cancel out.
```python
from typing import List

class Solution:
    def singleNonDuplicate(self, nums: List[int]) -> int:
        answer = 0
        for num in nums:
            answer ^= num
        return answer
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search using even indices as pair starts.
```python
from typing import List

class Solution:
    def singleNonDuplicate(self, nums: List[int]) -> int:
        left, right = 0, len(nums) - 1
        while left < right:
            mid = (left + right) // 2
            if mid % 2 == 1:
                mid -= 1
            if nums[mid] == nums[mid + 1]:
                left = mid + 2
            else:
                right = mid
        return nums[left]
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- The single element may be at either end.
- Force `mid` to an even index before comparing with `mid + 1`.
- Do not use XOR as the optimal answer if the question requires O(log n).

## Related
- Find First and Last Position of Element in Sorted Array
- Missing Number

