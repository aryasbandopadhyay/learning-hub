# 22. Intersection of Two Arrays II

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given two integer arrays `nums1` and `nums2`, return their multiset intersection. Each value appears in the result as many times as it appears in both arrays, using the smaller frequency.

**Input**
- `nums1`: the first list of integers.
- `nums2`: the second list of integers.

**Output**
- A list containing the common values with multiplicity. **This judge compares exactly**; the reference scans the longer array after counting the shorter one, so return matches in that scan order.

## Constraints
- `1 <= nums1.length, nums2.length <= 1000`
- `0 <= nums1[i], nums2[i] <= 1000`

## Examples
```text
Input: nums1 = [1,2,2,1], nums2 = [2,2]
Output: [2,2]
Explanation: The value `2` appears twice in both arrays, so the intersection contains two copies: `[2,2]`.
```

## Understanding & Intuition
Multiplicity requires counts, not just membership. Sorting enables two pointers; hashing the smaller array gives linear expected time.

## Approach 1 — Naive / Brute Force
**Idea:** Match each nums1 value to one unused nums2 value.
```python
class Solution:
    def intersect(self, nums1: list[int], nums2: list[int]) -> list[int]:
        used = [False] * len(nums2)
        ans = []
        for x in nums1:
            for i, y in enumerate(nums2):
                if not used[i] and x == y:
                    used[i] = True
                    ans.append(x)
                    break
        return ans
```
- **Time:** O(m*n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort both arrays and use two pointers.
```python
class Solution:
    def intersect(self, nums1: list[int], nums2: list[int]) -> list[int]:
        nums1.sort(); nums2.sort()
        i = j = 0
        ans = []
        while i < len(nums1) and j < len(nums2):
            if nums1[i] == nums2[j]:
                ans.append(nums1[i]); i += 1; j += 1
            elif nums1[i] < nums2[j]:
                i += 1
            else:
                j += 1
        return ans
```
- **Time:** O(m log m + n log n) — **Space:** O(1) extra

## Approach 3 — Optimal
**Idea:** Count smaller array and consume counts.
```python
class Solution:
    def intersect(self, nums1: list[int], nums2: list[int]) -> list[int]:
        if len(nums1) > len(nums2):
            nums1, nums2 = nums2, nums1
        counts = {}
        for x in nums1:
            counts[x] = counts.get(x, 0) + 1
        ans = []
        for x in nums2:
            if counts.get(x, 0) > 0:
                ans.append(x)
                counts[x] -= 1
        return ans
```
- **Time:** O(m+n) — **Space:** O(min(m,n))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m*n) | O(n) |
| Better | O(m log m + n log n) | O(1) extra |
| Optimal | O(m+n) | O(min(m,n)) |

## Edge Cases & Pitfalls
- Preserve multiplicity.
- Order is irrelevant.
- Counting smaller input saves space.

## Related
- Intersection of Two Arrays
- Valid Anagram
