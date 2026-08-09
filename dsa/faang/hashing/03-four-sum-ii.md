# 03. Four Sum II

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given four integer arrays `nums1`, `nums2`, `nums3`, and `nums4` of equal length, return the number of tuples `(i, j, k, l)` such that `nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0`.

**Input**
- `nums1`: a `list[int]`; the first integer array.
- `nums2`: a `list[int]`; the second integer array.
- `nums3`: a `list[int]`; the third integer array.
- `nums4`: a `list[int]`; the fourth integer array.

**Output**
- A `int`. Return the number of tuples `(i, j, k, l)` such that `nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0`.

## Constraints
- `1 <= len(nums1) <= 200`, all four arrays have the same length, and values fit in signed 32-bit integers.

## Examples
```text
Input: nums1 = [1, 2], nums2 = [-2, -1], nums3 = [-1, 2], nums4 = [0, 2]
Output: 2
Explanation: The valid tuples are (0,0,0,1) and (1,1,0,0).
```

## Understanding & Intuition
The equation can be split into two independent pair sums: `a + b = -(c + d)`. Counting pair sums from two arrays turns a four-dimensional search into hashmap lookups. Duplicate values must contribute multiplicity, not just existence.

## Approach 1 — Naive / Brute Force
**Idea:** Check every quadruple directly.
```python
class Solution:
    def fourSumCount(self, nums1: list[int], nums2: list[int], nums3: list[int], nums4: list[int]) -> int:
        ans = 0
        for a in nums1:
            for b in nums2:
                for c in nums3:
                    for d in nums4:
                        if a + b + c + d == 0:
                            ans += 1
        return ans
```
- **Time:** O(n⁴) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Store sorted sums of `nums3 + nums4`, then binary-search how many equal each needed complement.
```python
class Solution:
    def fourSumCount(self, nums1, nums2, nums3, nums4):
        from bisect import bisect_left, bisect_right
        cd = []
        for c in nums3:
            for d in nums4:
                cd.append(c + d)
        cd.sort()
        ans = 0
        for a in nums1:
            for b in nums2:
                need = -(a + b)
                ans += bisect_right(cd, need) - bisect_left(cd, need)
        return ans
```
- **Time:** O(n² log n) — **Space:** O(n²)

## Approach 3 — Optimal
**Idea:** Count sums of `nums1 + nums2`, then scan `nums3 + nums4` and add complement frequencies.
```python
class Solution:
    def fourSumCount(self, nums1, nums2, nums3, nums4):
        counts = {}
        for a in nums1:
            for b in nums2:
                s = a + b
                counts[s] = counts.get(s, 0) + 1
        ans = 0
        for c in nums3:
            for d in nums4:
                ans += counts.get(-(c + d), 0)
        return ans
```
- **Time:** O(n²) — **Space:** O(n²)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n⁴) | O(1) |
| Better | O(n² log n) | O(n²) |
| Optimal | O(n²) | O(n²) |

## Edge Cases & Pitfalls
- Count tuples by indices, so duplicates multiply the answer.
- Negative values and zero require no special casing.
- Use a count map, not a set of pair sums.

## Related
- 4Sum
- Two Sum
