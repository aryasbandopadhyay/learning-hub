# 07. Median of Two Sorted Arrays

- **Difficulty:** Hard
- **Pattern:** Binary Search
- **Asked at:** Google, Apple, Microsoft, Meta

## Problem
Given two sorted arrays `nums1` and `nums2`, return the median of all values from both arrays
combined. The merged length may be odd or even, but you do not need to actually merge the arrays.

**Input**
- `nums1`: the first sorted integer list.
- `nums2`: the second sorted integer list.

**Output**
- A floating-point number: the median of the combined multiset of values.

## Constraints
- 0 <= nums1.length, nums2.length <= 1000
- 1 <= nums1.length + nums2.length <= 2000
- -10^6 <= nums1[i], nums2[i] <= 10^6
- Both arrays are sorted in ascending order.

## Examples
```text
Input: nums1 = [1,3], nums2 = [2]
Output: 2.0
Explanation: The combined sorted values are `[1,2,3]`, whose middle value is `2.0`.
```

## Understanding & Intuition
The median splits the combined array into equal left and right halves. We can binary search how many values to take from the smaller array for the left half. A partition is valid when every left-side value is less than or equal to every right-side value.

## Approach 1 — Naive / Brute Force
**Idea:** Merge both arrays fully and read the middle.
```python
from typing import List

class Solution:
    def findMedianSortedArrays(self, nums1: List[int], nums2: List[int]) -> float:
        merged = sorted(nums1 + nums2)
        n = len(merged)
        if n % 2:
            return float(merged[n // 2])
        return (merged[n // 2 - 1] + merged[n // 2]) / 2
```
- **Time:** O((m+n) log(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** Merge only until the median positions are reached.
```python
from typing import List

class Solution:
    def findMedianSortedArrays(self, nums1: List[int], nums2: List[int]) -> float:
        total = len(nums1) + len(nums2)
        need = total // 2
        i = j = 0
        prev = curr = 0
        for _ in range(need + 1):
            prev = curr
            if j == len(nums2) or (i < len(nums1) and nums1[i] <= nums2[j]):
                curr = nums1[i]
                i += 1
            else:
                curr = nums2[j]
                j += 1
        if total % 2:
            return float(curr)
        return (prev + curr) / 2
```
- **Time:** O(m+n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search a valid partition on the smaller array.
```python
from typing import List

class Solution:
    def findMedianSortedArrays(self, nums1: List[int], nums2: List[int]) -> float:
        if len(nums1) > len(nums2):
            nums1, nums2 = nums2, nums1

        m, n = len(nums1), len(nums2)
        half = (m + n + 1) // 2
        left, right = 0, m

        while left <= right:
            i = (left + right) // 2
            j = half - i
            a_left = float("-inf") if i == 0 else nums1[i - 1]
            a_right = float("inf") if i == m else nums1[i]
            b_left = float("-inf") if j == 0 else nums2[j - 1]
            b_right = float("inf") if j == n else nums2[j]

            if a_left <= b_right and b_left <= a_right:
                if (m + n) % 2:
                    return float(max(a_left, b_left))
                return (max(a_left, b_left) + min(a_right, b_right)) / 2
            if a_left > b_right:
                right = i - 1
            else:
                left = i + 1
        return 0.0
```
- **Time:** O(log min(m,n)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((m+n) log(m+n)) | O(m+n) |
| Better | O(m+n) | O(1) |
| Optimal | O(log min(m,n)) | O(1) |

## Edge Cases & Pitfalls
- Always binary search the smaller array.
- Sentinels handle empty partitions.
- For even length, average max-left and min-right.

## Related
- Kth Smallest Element in a Sorted Matrix
- Merge Sorted Array

