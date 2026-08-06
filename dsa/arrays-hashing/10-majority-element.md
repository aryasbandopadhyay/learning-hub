# 10. Majority Element

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Return the element appearing more than `floor(n/2)` times. A majority element is guaranteed. Constraints: `1 <= n <= 5 * 10^4`.

## Examples
```text
Input: nums = [3,2,3]
Output: 3
Explanation: 3 appears more than half the time.
```

## Understanding & Intuition
The majority outnumbers all other values combined. Sorting puts it in the middle; Boyer-Moore cancels different pairs and keeps the survivor.

## Approach 1 — Naive / Brute Force
**Idea:** Count every candidate by scanning.
```python
class Solution:
    def majorityElement(self, nums: list[int]) -> int:
        for x in nums:
            if sum(1 for y in nums if y == x) > len(nums) // 2:
                return x
        return nums[0]
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort and take the middle element.
```python
class Solution:
    def majorityElement(self, nums: list[int]) -> int:
        return sorted(nums)[len(nums)//2]
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use Boyer-Moore voting.
```python
class Solution:
    def majorityElement(self, nums: list[int]) -> int:
        cand = None
        votes = 0
        for x in nums:
            if votes == 0:
                cand = x
            votes += 1 if x == cand else -1
        return cand
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Majority is guaranteed.
- Verify candidate if guarantee is removed.
- Initialize votes at 0.

## Related
- Majority Element II
- Find All Duplicates in an Array
