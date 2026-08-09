# 15. Merge Sorted Array

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given two non-decreasing integer arrays, merge them into `nums1` in non-decreasing order. `nums1` has length `m + n`: its first `m` entries are real values and its last `n` entries are placeholders.

**Input**
- `nums1`: the destination list containing `m` sorted values followed by `n` placeholders.
- `m`: the number of valid values initially in `nums1`.
- `nums2`: the second sorted list.
- `n`: the number of values in `nums2`.

**Output**
- Modify `nums1` in-place so it contains all `m + n` values sorted in non-decreasing order. **This judge compares exactly** to the final `nums1` contents.

## Constraints
- `nums1.length == m + n`
- `nums2.length == n`
- `0 <= m, n <= 200`
- `1 <= m + n <= 200`
- `-10^9 <= nums1[i], nums2[j] <= 10^9`

## Examples
```text
Input: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
Output: [1,2,2,3,5,6]
Explanation: The valid values `[1,2,3]` and `[2,5,6]` merge into `[1,2,2,3,5,6]` in sorted order.
```

## Understanding & Intuition
The spare space at the end is useful. Merging backward avoids overwriting unprocessed values from `nums1`.

## Approach 1 — Naive / Brute Force
**Idea:** Fill spare slots and bubble sort.
```python
class Solution:
    def merge(self, nums1: list[int], m: int, nums2: list[int], n: int) -> None:
        for i in range(n):
            nums1[m+i] = nums2[i]
        for _ in range(m+n):
            for i in range(1, m+n):
                if nums1[i-1] > nums1[i]:
                    nums1[i-1], nums1[i] = nums1[i], nums1[i-1]
```
- **Time:** O((m+n)^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Copy the valid part of nums1 and merge forward.
```python
class Solution:
    def merge(self, nums1: list[int], m: int, nums2: list[int], n: int) -> None:
        left = nums1[:m]
        i = j = k = 0
        while i < m and j < n:
            if left[i] <= nums2[j]:
                nums1[k] = left[i]; i += 1
            else:
                nums1[k] = nums2[j]; j += 1
            k += 1
        while i < m:
            nums1[k] = left[i]; i += 1; k += 1
        while j < n:
            nums1[k] = nums2[j]; j += 1; k += 1
```
- **Time:** O(m+n) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Merge from the end into the spare slots.
```python
class Solution:
    def merge(self, nums1: list[int], m: int, nums2: list[int], n: int) -> None:
        i, j, w = m - 1, n - 1, m + n - 1
        while j >= 0:
            if i >= 0 and nums1[i] > nums2[j]:
                nums1[w] = nums1[i]
                i -= 1
            else:
                nums1[w] = nums2[j]
                j -= 1
            w -= 1
```
- **Time:** O(m+n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((m+n)^2) | O(1) |
| Better | O(m+n) | O(m) |
| Optimal | O(m+n) | O(1) |

## Edge Cases & Pitfalls
- Only first m values in nums1 are input.
- Duplicates are preserved.
- If nums2 is exhausted, stop.

## Related
- Merge Two Sorted Lists
- Sort Colors
