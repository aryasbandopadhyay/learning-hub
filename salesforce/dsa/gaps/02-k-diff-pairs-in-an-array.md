# 02. K-diff Pairs in an Array

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Return the number of unique value pairs whose absolute difference is exactly `k`.

## Examples
```text
Input: nums = [3,1,4,1,5], k = 2
Output: 2
Explanation: The pairs are (1,3) and (3,5).
```

## Understanding & Intuition
Pairs are unique by value. For `k > 0`, check whether `x + k` exists; for `k == 0`, only values with duplicate counts count.

## Approach 1 — Naive / Brute Force
**Idea:** Check every index pair and store normalized value pairs.
```python
class Solution:
    def findPairs(self, nums: list[int], k: int) -> int:
        pairs = set()
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                if abs(nums[i] - nums[j]) == k:
                    pairs.add(tuple(sorted((nums[i], nums[j]))))
        return len(pairs)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort and use two pointers while skipping duplicate values.
```python
class Solution:
    def findPairs(self, nums: list[int], k: int) -> int:
        if k < 0:
            return 0
        nums.sort(); ans = 0; left = 0; right = 1
        while right < len(nums):
            if left == right or nums[right] - nums[left] < k:
                right += 1
            elif nums[right] - nums[left] > k:
                left += 1
            else:
                ans += 1; a, b = nums[left], nums[right]
                while left < len(nums) and nums[left] == a: left += 1
                while right < len(nums) and nums[right] == b: right += 1
        return ans
```
- **Time:** O(n log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Count frequencies once and handle the zero-difference case separately.
```python
class Solution:
    def findPairs(self, nums: list[int], k: int) -> int:
        if k < 0:
            return 0
        counts = {}
        for x in nums:
            counts[x] = counts.get(x, 0) + 1
        if k == 0:
            return sum(freq > 1 for freq in counts.values())
        return sum(x + k in counts for x in counts)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- `k == 0` requires duplicate values.
- Negative `k` cannot be an absolute difference.
- Do not count `(a,b)` and `(b,a)` separately.

## Related
- Two Sum
- Contains Duplicate
