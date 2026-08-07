# 28. Rotate Array

- **Difficulty:** Medium
- **Pattern:** Arrays / Two Pointers
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Rotate an array to the right by `k` steps in-place.

## Examples
```text
Input: nums = [1,2,3,4,5,6,7], k = 3
Output: [5,6,7,1,2,3,4]
Explanation: The last three elements move to the front.
```

## Understanding & Intuition
Right rotation places the suffix of length `k` before the prefix. Reversing the whole array and then both parts achieves that order in-place.

## Approach 1 — Naive / Brute Force
**Idea:** Rotate one step at a time.
```python
class Solution:
    def rotate(self, nums: list[int], k: int) -> None:
        n = len(nums); k %= n
        for _ in range(k):
            last = nums[-1]
            for i in range(n - 1, 0, -1): nums[i] = nums[i - 1]
            nums[0] = last
```
- **Time:** O(nk) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build the rotated order in an auxiliary list, then assign back.
```python
class Solution:
    def rotate(self, nums: list[int], k: int) -> None:
        n = len(nums); k %= n
        nums[:] = nums[-k:] + nums[:-k] if k else nums[:]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Reverse the whole array, then reverse the first `k` and the rest.
```python
class Solution:
    def rotate(self, nums: list[int], k: int) -> None:
        n = len(nums); k %= n
        def rev(l: int, r: int) -> None:
            while l < r:
                nums[l], nums[r] = nums[r], nums[l]; l += 1; r -= 1
        rev(0, n - 1); rev(0, k - 1); rev(k, n - 1)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nk) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Reduce `k` modulo `n`.
- Mutate `nums` in-place.
- LeetCode input is non-empty.

## Related
- Reverse Words in a String II
- Move Zeroes
