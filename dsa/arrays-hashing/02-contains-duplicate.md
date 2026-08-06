# 02. Contains Duplicate

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Apple

## Problem
Return `True` if any integer appears at least twice in `nums`; otherwise return `False`. Constraints: `1 <= n <= 10^5`.

## Examples
```text
Input: nums = [1,2,3,1]
Output: True
Explanation: 1 appears twice.
```

## Understanding & Intuition
Duplicate detection is membership tracking. Brute force compares all pairs; sorting makes duplicates adjacent; a set gives linear-time early exit.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every pair.
```python
class Solution:
    def containsDuplicate(self, nums: list[int]) -> bool:
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                if nums[i] == nums[j]:
                    return True
        return False
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort and check adjacent values.
```python
class Solution:
    def containsDuplicate(self, nums: list[int]) -> bool:
        arr = sorted(nums)
        for i in range(1, len(arr)):
            if arr[i] == arr[i - 1]:
                return True
        return False
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Remember values in a set.
```python
class Solution:
    def containsDuplicate(self, nums: list[int]) -> bool:
        seen = set()
        for x in nums:
            if x in seen:
                return True
            seen.add(x)
        return False
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Length 1 returns False.
- Negative numbers are normal keys.
- Sorting a copy avoids mutation.

## Related
- Contains Duplicate II
- Set Mismatch
